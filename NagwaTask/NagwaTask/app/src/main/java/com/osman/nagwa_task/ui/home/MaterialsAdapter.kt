package com.osman.nagwa_task.ui.home

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.osman.nagwa_task.R
import com.osman.nagwa_task.data.Material
import com.osman.nagwa_task.databinding.ItemViewVideoBinding


class MaterialsAdapter : RecyclerView.Adapter<MaterialsAdapter.VideoItemHolder>() {

    private var recyclerView: RecyclerView? = null
    private var currentHolder: VideoItemHolder? = null
        set(value) {
//            field?.pausePlayer()
            field = value
        }
    private var currentPlayed = -1
        set(value) {
            val old = field
//            (recyclerView?.findViewHolderForAdapterPosition(old) as? VideoItemHolder)?.pausePlayer()
//            notifyItemChanged(field)
            field = value
//            notifyItemChanged(old)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemHolder {
//        recyclerView = parent as? RecyclerView
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewVideoBinding.inflate(inflater, parent, false)
        return VideoItemHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoItemHolder, position: Int) {
        val material = Material(
            position,
            "Item $position",
//                "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
            "https://bestvpn.org/html5demos/assets/dizzy.mp4"
        )
        if (holder.simpleExoplayer == null) {
            holder.initializePlayer(material)
        } else {
            if (currentPlayed != position) {
                if (holder.isPlaying()) {
                    holder.pausePlayer()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return 5
    }

//    override fun onViewRecycled(holder: VideoItemHolder) {
//        holder.simpleExoplayer?.pause()
//        super.onViewRecycled(holder)
//    }

    inner class VideoItemHolder(private val binding: ItemViewVideoBinding) :
        RecyclerView.ViewHolder(binding.root), Player.Listener {

        var simpleExoplayer: SimpleExoPlayer? = null

        fun initializePlayer(material: Material) {
            simpleExoplayer = SimpleExoPlayer.Builder(binding.root.context)
                .build()
                .also { exoPlayer ->
                    val mediaItem = MediaItem.fromUri(material.url.toUri())
                    exoPlayer.setMediaItem(mediaItem)
//                    exoPlayer.playWhenReady = false
                    binding.exoplayerView.player = exoPlayer
                    exoPlayer.addListener(this)
//                    exoPlayer.prepare()
                }

            binding.exoplayerView.setShutterBackgroundColor(Color.TRANSPARENT)
            binding.exoplayerView.findViewById<View>(R.id.exo_shutter)
                ?.setBackgroundResource(R.drawable.bg_launcher)
            binding.exoplayerView.findViewById<TextView>(R.id.video_title)?.text = material.title
//            binding.exoplayerView.findViewById<View>(R.id.exo_play)?.setOnClickListener {
//                Toast.makeText(it.context, "GGGGGGGGGG", Toast.LENGTH_SHORT).show()
//                startPlayer()
//            }
        }

        fun releasePlayer() {
            simpleExoplayer?.run {
                release()
                removeListener(this@VideoItemHolder)
            }
            simpleExoplayer = null
        }

        override fun onPlayerError(error: PlaybackException) {
            // handle error
        }

        fun pausePlayer() {
            Log.w("ssss", "pausePlayer: ")
            simpleExoplayer?.let {
                it.playWhenReady = false
                it.playbackState
            }
        }

        fun startPlayer() {
            Log.w("ssss", "startPlayer: ")
            simpleExoplayer?.let {
                it.playWhenReady = true
                it.prepare()
            }
        }

        fun isPlaying(): Boolean {
            return simpleExoplayer != null
                    && simpleExoplayer!!.playbackState != Player.STATE_ENDED
                    && simpleExoplayer!!.playbackState != Player.STATE_IDLE
                    && simpleExoplayer!!.playWhenReady
        }


        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(
                "exoooo",
                "changed state to $stateString & playWhenReady: $playWhenReady & isPlaying: ${simpleExoplayer?.isPlaying} & isPlaying(): ${isPlaying()}"
            )

            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Player.STATE_READY -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                Player.STATE_ENDED -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                Player.STATE_IDLE -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
//            if (isPlaying && isPlaying() && currentPlayed != bindingAdapterPosition) {
//                currentPlayed = bindingAdapterPosition
//            }
            if (isPlaying) {
                currentPlayed = bindingAdapterPosition
//                currentHolder = this@VideoItemHolder
            }
        }
    }
}