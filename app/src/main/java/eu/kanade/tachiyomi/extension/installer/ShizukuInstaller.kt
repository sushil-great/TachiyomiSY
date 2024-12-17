package eu.kanade.tachiyomi.extension.installer

import android.app.Service
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import eu.kanade.tachiyomi.extension.model.InstallStep
import eu.kanade.tachiyomi.util.system.getUriSize
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import logcat.LogPriority
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import tachiyomi.core.common.util.system.logcat
import tachiyomi.i18n.MR
import java.io.BufferedReader
import java.io.InputStream
import java.lang.reflect.Method

class ShizukuInstaller(private val service: Service) : Installer(service) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val shizukuDeadListener = Shizuku.OnBinderDeadListener {
        logcat { "Shizuku was killed prematurely" }
        service.stopSelf()
    }

    private val shizukuPermissionListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    ready = true
                    checkQueue()
                } else {
                    service.stopSelf()
                }
                Shizuku.removeRequestPermissionResultListener(this)
            }
        }
    }

    override var ready = false

    override fun processEntry(entry: Entry) {
        super.processEntry(entry)
        scope.launch {
            var sessionId: String? = null
            try {
                val size = service.getUriSize(entry.uri) ?: throw IllegalStateException()
                service.contentResolver.openInputStream(entry.uri)!!.use {
                    val createCommand = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val userId = Process.myUserHandle().hashCode()
                        "pm install-create --user $userId -r -i ${service.packageName} -S $size"
                    } else {
                        "pm install-create -r -i ${service.packageName} -S $size"
                    }
                    val createResult = exec(createCommand)
                    sessionId = SESSION_ID_REGEX.find(createResult.out)?.value
                        ?: throw RuntimeException("Failed to create install session")

                    val writeResult = exec("pm install-write -S $size $sessionId base -", it)
                    if (writeResult.resultCode != 0) {
                        throw RuntimeException("Failed to write APK to session $sessionId")
                    }

                    val commitResult = exec("pm install-commit $sessionId")
                    if (commitResult.resultCode != 0) {
                        throw RuntimeException("Failed to commit install session $sessionId")
                    }

                    continueQueue(InstallStep.Installed)
                }
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e) { "Failed to install extension ${entry.downloadId} ${entry.uri}" }
                if (sessionId != null) {
                    exec("pm install-abandon $sessionId")
                }
                continueQueue(InstallStep.Error)
            }
        }
    }

    // Don't cancel if entry is already started installing
    override fun cancelEntry(entry: Entry): Boolean = getActiveEntry() != entry

    override fun onDestroy() {
        Shizuku.removeBinderDeadListener(shizukuDeadListener)
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
        scope.cancel()
        super.onDestroy()
    }

    private val newProcess: Method
    private fun exec(command: String, stdin: InputStream? = null): ShellResult {
        val process = newProcess.invoke(null, arrayOf("sh", "-c", command), null, null) as ShizukuRemoteProcess
        if (stdin != null) {
            process.outputStream.use { stdin.copyTo(it) }
        }
        val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
        val resultCode = process.waitFor()
        return ShellResult(resultCode, output)
    }

    private data class ShellResult(val resultCode: Int, val out: String)

    init {
        Shizuku.addBinderDeadListener(shizukuDeadListener)
        ready = if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
                false
            }
        } else {
            logcat(LogPriority.ERROR) { "Shizuku is not ready to use" }
            service.toast(MR.strings.ext_installer_shizuku_stopped)
            service.stopSelf()
            false
        }
        newProcess = Shizuku::class.java
            .getDeclaredMethod("newProcess", Array<out String>::class.java, Array<out String>::class.java, String::class.java)
        newProcess.isAccessible = true
    }
}

private const val SHIZUKU_PERMISSION_REQUEST_CODE = 14045
private val SESSION_ID_REGEX = Regex("(?<=\\[).+?(?=])")
