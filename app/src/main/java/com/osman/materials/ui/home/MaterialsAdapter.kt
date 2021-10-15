package com.osman.materials.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.osman.materials.R
import com.osman.materials.databinding.ItemViewPdfBinding
import com.osman.materials.databinding.ItemViewUnknownBinding
import com.osman.materials.databinding.ItemViewVideoBinding
import com.osman.materials.domain.model.Material
import com.osman.materials.ui.splash.loadImageToBackground
import com.osman.materials.utils.DownloadButtonProgress
import java.io.File


class MaterialsAdapter(
    private val onItemDownloadClick: (Int, String) -> Unit,
    private val onItemClick: (Material) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var values: MutableList<Material> = ArrayList()
        set(value) {
            val oldList = ArrayList(field)
            field.clear()
            field.addAll(value)
            val newList = ArrayList(field)
            val diffCallback = MaterialsDiffCallback(oldList, newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
        }

    private var recyclerView: RecyclerView? = null

    private var currentPlayed = -1
        set(value) {
            (recyclerView?.findViewHolderForAdapterPosition(field) as? VideoItemHolder)?.pausePlayer()
            field = value
        }

    override fun getItemViewType(position: Int): Int {
        return values[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        recyclerView = parent as? RecyclerView
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Material.Type.VIDEO.value -> {
                val binding = ItemViewVideoBinding.inflate(inflater, parent, false)
                VideoItemHolder(binding)
            }
            Material.Type.PDF.value -> {
                val binding = ItemViewPdfBinding.inflate(inflater, parent, false)
                PdfItemHolder(binding)
            }
            else -> {
                val binding = ItemViewUnknownBinding.inflate(inflater, parent, false)
                UnKnownItemHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val material = values[position]
        if (holder is VideoItemHolder) {
//            if (holder.simpleExoplayer == null) {
            holder.initializePlayer(material)
//            }
        } else if (holder is PdfItemHolder) {
            holder.bind(material)
        }
        holder.itemView.setOnClickListener { onItemClick(material) }
    }

    override fun getItemCount() = values.size

    fun setItemDownloadProgress(position: Int, progress: Int, filePath: String? = null) {
        val holder = recyclerView?.findViewHolderForAdapterPosition(position) ?: return
        val btnDownload: DownloadButtonProgress = when (holder) {
            is VideoItemHolder -> holder.binding.btnDownload
            is PdfItemHolder -> holder.binding.btnDownload
            else -> return
        }
        val downloadProgress: TextView = when (holder) {
            is VideoItemHolder -> holder.binding.downloadProgress
            is PdfItemHolder -> holder.binding.downloadProgress
            else -> return
        }
        when (progress) {
            -1 -> {
                btnDownload.setIdle()
                downloadProgress.text = "0%"
                downloadProgress.visibility = View.GONE
            }
            0 -> {
                btnDownload.setIndeterminate()
                downloadProgress.text = "0%"
                downloadProgress.visibility = View.VISIBLE
            }
            100 -> {
                btnDownload.setFinish()
                downloadProgress.text = "100%"
                downloadProgress.visibility = View.GONE
            }
            else -> {
                btnDownload.setDeterminate()
                btnDownload.currentProgress = progress
                downloadProgress.text = "$progress%"
                downloadProgress.visibility = View.VISIBLE
            }
        }
        if (progress == 100) {
            values[position] = values[position].apply {
                isLocale = true
                url = filePath ?: ""
            }
            notifyItemChanged(position)
        }
    }

    inner class UnKnownItemHolder(binding: ItemViewUnknownBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class PdfItemHolder(val binding: ItemViewPdfBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(material: Material) {
            if (material.isLocale) {
                binding.pdfView.visibility = View.VISIBLE
                binding.pdfWebView.visibility = View.GONE
                val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        binding.pdfView.context,
                        binding.pdfView.context.packageName.toString() + ".provider",
                        File(material.url)
                    )
                } else {
                    Uri.parse(material.url)
                }
                binding.pdfView.fromUri(uri)
                    .defaultPage(0)
                    .spacing(0)
                    .load()
            } else {
                binding.pdfView.visibility = View.GONE
                binding.pdfWebView.visibility = View.VISIBLE
                @SuppressLint("SetJavaScriptEnabled")
                binding.pdfWebView.settings.javaScriptEnabled = true
                binding.pdfWebView.setOnTouchListener { _, _ -> true }
                binding.pdfWebView.loadUrl("https://docs.google.com/viewer?url=${material.url}")
            }
            binding.pdfTitle.text = material.title

            if (material.isLocale) {
                binding.btnDownload.setFinish()
            }

            binding.btnDownload.addOnClickListener(object : DownloadButtonProgress.OnClickListener {
                override fun onIdleButtonClick(view: View?) {
                    binding.btnDownload.setIndeterminate()
                    onItemDownloadClick(bindingAdapterPosition, material.url)
                }

                override fun onCancelButtonClick(view: View?) {

                }

                override fun onFinishButtonClick(view: View?) {
                }

            })
        }
    }

    inner class VideoItemHolder(val binding: ItemViewVideoBinding) :
        RecyclerView.ViewHolder(binding.root), Player.Listener {

        var simpleExoplayer: SimpleExoPlayer? = null
        var videoTitle: TextView? = null
        var exoShutter: View? = null

        fun initializePlayer(material: Material) {
            simpleExoplayer = SimpleExoPlayer.Builder(binding.root.context)
                .build()
                .also { exoPlayer ->
                    val mediaItem = MediaItem.fromUri(material.url.toUri())
                    exoPlayer.setMediaItem(mediaItem)
                    binding.exoplayerView.player = exoPlayer
                    exoPlayer.addListener(this)
                }

            binding.exoplayerView.setControllerVisibilityListener {
                if (binding.btnDownload.currState == DownloadButtonProgress.STATE_FINISHED) {
                    binding.btnDownload.visibility = it
                }
            }

            binding.exoplayerView.setShutterBackgroundColor(Color.TRANSPARENT)
            exoShutter = binding.exoplayerView.findViewById(R.id.exo_shutter)
            videoTitle = binding.exoplayerView.findViewById(R.id.video_title)
            videoTitle?.text = material.title

            exoShutter?.loadImageToBackground(material.url)

            if (material.isLocale) {
                binding.btnDownload.setFinish()
            }

            binding.btnDownload.addOnClickListener(object : DownloadButtonProgress.OnClickListener {
                override fun onIdleButtonClick(view: View?) {
                    binding.btnDownload.setIndeterminate()
                    onItemDownloadClick(bindingAdapterPosition, material.url)
                }

                override fun onCancelButtonClick(view: View?) {

                }

                override fun onFinishButtonClick(view: View?) {
                }

            })
        }

        override fun onPlayerError(error: PlaybackException) {
            // handle error
        }

        fun startPlayer() {
            simpleExoplayer?.let {
                it.playWhenReady = true
                it.prepare()
            }
        }

        fun pausePlayer() {
            simpleExoplayer?.let {
                it.playWhenReady = false
                it.playbackState
            }
        }

        fun releasePlayer() {
            simpleExoplayer?.run {
                release()
                removeListener(this@VideoItemHolder)
            }
            simpleExoplayer = null
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Player.STATE_READY, Player.STATE_ENDED, Player.STATE_IDLE -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                currentPlayed = bindingAdapterPosition
            }
        }
    }

    private class MaterialsDiffCallback(
        private val oldList: List<Material>,
        private val newList: List<Material>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition] === newList[newPosition]
        }

        @Nullable
        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            return super.getChangePayload(oldPosition, newPosition)
        }
    }


}