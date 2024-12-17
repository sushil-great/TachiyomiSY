@file:Suppress("ChromeOsAbiSupport")

import mihon.buildlogic.getBuildTime
import mihon.buildlogic.getCommitCount
import mihon.buildlogic.getGitSha

plugins {
    id("mihon.android.application")
    id("mihon.android.application.compose")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
    // id("com.github.zellius.shortcut-helper")
    alias(libs.plugins.aboutLibraries)
    id("com.github.ben-manes.versions")
}

if (gradle.startParameter.taskRequests.toString().contains("Standard")) {
    pluginManager.apply {
        apply(libs.plugins.google.services.get().pluginId)
        apply(libs.plugins.firebase.crashlytics.get().pluginId)
    }
}

// shortcutHelper.setFilePath("./shortcuts.xml")

val supportedAbis = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

android {
    namespace = "eu.kanade.tachiyomi"

    defaultConfig {
        applicationId = "eu.kanade.tachiyomi.sy"

        versionCode = 71
        versionName = "1.11.0"

        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")
        buildConfigField("boolean", "INCLUDE_UPDATER", "false")

        ndk {
            abiFilters += supportedAbis
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*supportedAbis.toTypedArray())
            isUniversalApk = true
        }
    }

    buildTypes {
        named("debug") {
            versionNameSuffix = "-${getCommitCount()}"
            applicationIdSuffix = ".debug"
            isPseudoLocalesEnabled = true
        }
        create("releaseTest") {
            applicationIdSuffix = ".rt"
            // isMinifyEnabled = true
            // isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
            matchingFallbacks.add("release")
        }
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
        create("benchmark") {
            initWith(getByName("release"))

            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            isDebuggable = false
            isProfileable = true
            versionNameSuffix = "-benchmark"
            applicationIdSuffix = ".benchmark"
        }
    }

    sourceSets {
        getByName("benchmark").res.srcDirs("src/debug/res")
    }

    flavorDimensions.add("default")

    productFlavors {
        create("standard") {
            buildConfigField("boolean", "INCLUDE_UPDATER", "true")
            dimension = "default"
        }
        create("fdroid") {
            dimension = "default"
        }
        create("dev") {
            // Include pseudolocales: https://developer.android.com/guide/topics/resources/pseudolocales
            resourceConfigurations.addAll(listOf("en", "en_XA", "ar_XB", "xxhdpi"))
            dimension = "default"
        }
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "kotlin-tooling-metadata.json",
                "META-INF/DEPENDENCIES",
                "LICENSE.txt",
                "META-INF/LICENSE",
                "META-INF/**/LICENSE.txt",
                "META-INF/*.properties",
                "META-INF/**/*.properties",
                "META-INF/README.md",
                "META-INF/NOTICE",
                "META-INF/*.version",
            ),
        )
    }

    dependenciesInfo {
        includeInApk = false
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true

        // Disable some unused things
        aidl = false
        renderScript = false
        shaders = false
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=coil3.annotation.ExperimentalCoilApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

dependencies {
    implementation(projects.i18n)
    // SY -->
    implementation(projects.i18nSy)
    // SY <--
    implementation(projects.core.common)
    implementation(projects.coreMetadata)
    implementation(projects.sourceApi)
    implementation(projects.sourceLocal)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.presentationCore)
    implementation(projects.presentationWidget)

    // Compose
    implementation(compose.activity)
    implementation(compose.foundation)
    implementation(compose.material3.core)
    implementation(compose.material.icons)
    implementation(compose.animation)
    implementation(compose.animation.graphics)
    debugImplementation(compose.ui.tooling)
    implementation(compose.ui.tooling.preview)
    implementation(compose.ui.util)

    implementation(androidx.interpolator)

    implementation(androidx.paging.runtime)
    implementation(androidx.paging.compose)

    implementation(libs.bundles.sqlite)
    // SY -->
    implementation(sylibs.sqlcipher)
    // SY <--

    implementation(kotlinx.reflect)
    implementation(kotlinx.immutables)

    implementation(platform(kotlinx.coroutines.bom))
    implementation(kotlinx.bundles.coroutines)

    // AndroidX libraries
    implementation(androidx.annotation)
    implementation(androidx.appcompat)
    implementation(androidx.biometricktx)
    implementation(androidx.constraintlayout)
    implementation(androidx.corektx)
    implementation(androidx.splashscreen)
    implementation(androidx.recyclerview)
    implementation(androidx.viewpager)
    implementation(androidx.profileinstaller)

    implementation(androidx.bundles.lifecycle)

    // Job scheduling
    implementation(androidx.workmanager)

    // RxJava
    implementation(libs.rxjava)

    // Networking
    implementation(libs.bundles.okhttp)
    implementation(libs.okio)
    implementation(libs.conscrypt.android) // TLS 1.3 support for Android < 10

    // Data serialization (JSON, protobuf, xml)
    implementation(kotlinx.bundles.serialization)

    // HTML parser
    implementation(libs.jsoup)

    // Disk
    implementation(libs.disklrucache)
    implementation(libs.unifile)

    // Preferences
    implementation(libs.preferencektx)

    // Dependency injection
    implementation(libs.injekt.core)

    // Image loading
    implementation(platform(libs.coil.bom))
    implementation(libs.bundles.coil)
    implementation(libs.subsamplingscaleimageview) {
        exclude(module = "image-decoder")
    }
    implementation(libs.image.decoder)

    // UI libraries
    implementation(libs.material)
    implementation(libs.flexible.adapter.core)
    implementation(libs.photoview)
    implementation(libs.directionalviewpager) {
        exclude(group = "androidx.viewpager", module = "viewpager")
    }
    implementation(libs.insetter)
    implementation(libs.bundles.richtext)
    implementation(libs.aboutLibraries.compose)
    implementation(libs.bundles.voyager)
    implementation(libs.compose.materialmotion)
    implementation(libs.swipe)
    implementation(libs.compose.webview)
    implementation(libs.compose.grid)

    // Logging
    implementation(libs.logcat)

    // Crash reports/analytics
//    "standardImplementation"(platform(libs.firebase.bom))
//    "standardImplementation"(libs.firebase.analytics)
//    "standardImplementation"(libs.firebase.crashlytics)

    // Shizuku
    implementation(libs.bundles.shizuku)

    // Tests
    testImplementation(libs.bundles.test)

    // For detecting memory leaks; see https://square.github.io/leakcanary/
    // debugImplementation(libs.leakcanary.android)
    implementation(libs.leakcanary.plumber)

    testImplementation(kotlinx.coroutines.test)

    // SY -->
    // Text distance (EH)
    implementation(sylibs.simularity)

    // Firebase (EH)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Better logging (EH)
    implementation(sylibs.xlog)

    // RatingBar (SY)
    implementation(sylibs.ratingbar)
    implementation(sylibs.composeRatingbar)

    // Google drive
    implementation(sylibs.google.api.services.drive)
    implementation(sylibs.google.api.client.oauth)

    // Koin
    implementation(sylibs.koin.core)
    implementation(sylibs.koin.android)
}

androidComponents {
    beforeVariants { variantBuilder ->
        // Disables standardBenchmark
        if (variantBuilder.buildType == "benchmark") {
            variantBuilder.enable = variantBuilder.productFlavors.containsAll(
                listOf("default" to "dev"),
            )
        }
    }
    onVariants(selector().withFlavor("default" to "standard")) {
        // Only excluding in standard flavor because this breaks
        // Layout Inspector's Compose tree
        it.packaging.resources.excludes.add("META-INF/*.version")
    }
}

buildscript {
    dependencies {
        classpath(kotlinx.gradle)
    }
}
