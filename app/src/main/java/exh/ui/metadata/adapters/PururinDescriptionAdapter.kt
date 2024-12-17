package exh.ui.metadata.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.DescriptionAdapterPuBinding
import eu.kanade.tachiyomi.ui.manga.MangaScreenModel.State
import eu.kanade.tachiyomi.util.system.copyToClipboard
import exh.metadata.metadata.PururinSearchMetadata
import exh.ui.metadata.adapters.MetadataUIUtil.bindDrawable
import tachiyomi.core.common.i18n.pluralStringResource
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import kotlin.math.round

@Composable
fun PururinDescription(state: State.Success, openMetadataViewer: () -> Unit) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { factoryContext ->
            DescriptionAdapterPuBinding.inflate(LayoutInflater.from(factoryContext)).root
        },
        update = {
            val meta = state.meta
            if (meta == null || meta !is PururinSearchMetadata) return@AndroidView
            val binding = DescriptionAdapterPuBinding.bind(it)

            binding.genre.text =
                meta.tags.find { it.namespace == PururinSearchMetadata.TAG_NAMESPACE_CATEGORY }.let { genre ->
                    genre?.let { MetadataUIUtil.getGenreAndColour(context, it.name) }?.let {
                        binding.genre.setBackgroundColor(it.first)
                        it.second
                    } ?: genre?.name ?: context.stringResource(MR.strings.unknown)
                }

            binding.uploader.text = meta.uploaderDisp ?: meta.uploader.orEmpty()

            binding.size.text = meta.fileSize ?: context.stringResource(MR.strings.unknown)
            binding.size.bindDrawable(context, R.drawable.ic_outline_sd_card_24)

            binding.pages.text = context.pluralStringResource(SYMR.plurals.num_pages, meta.pages ?: 0, meta.pages ?: 0)
            binding.pages.bindDrawable(context, R.drawable.ic_baseline_menu_book_24)

            val ratingFloat = meta.averageRating?.toFloat()
            binding.ratingBar.rating = ratingFloat ?: 0F
            @SuppressLint("SetTextI18n")
            binding.rating.text =
                (round((ratingFloat ?: 0F) * 100.0) / 100.0).toString() + " - " +
                MetadataUIUtil.getRatingString(context, ratingFloat?.times(2))

            binding.moreInfo.bindDrawable(context, R.drawable.ic_info_24dp)

            listOf(
                binding.genre,
                binding.pages,
                binding.rating,
                binding.size,
                binding.uploader,
            ).forEach { textView ->
                textView.setOnLongClickListener {
                    context.copyToClipboard(
                        textView.text.toString(),
                        textView.text.toString(),
                    )
                    true
                }
            }

            binding.moreInfo.setOnClickListener {
                openMetadataViewer()
            }
        },
    )
}
