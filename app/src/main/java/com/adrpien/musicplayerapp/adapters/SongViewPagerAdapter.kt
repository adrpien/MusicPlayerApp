package com.adrpien.musicplayerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.data.entities.Song
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SongViewPagerAdapter (): BaseSongAdapter(R.layout.song_view_pager_row){

    override val differ: AsyncListDiffer<Song> = AsyncListDiffer(this, diffCallback)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val playerArtistTextView = findViewById<TextView>(R.id.songPagerRowArtistTextView)
            val playerTitleTextView = findViewById<TextView>(R.id.songPagerRowTitleTextView)
            playerTitleTextView.text = song.title
            playerArtistTextView.text = song.artist

            setItemClickListener {
                onItemClickListener?.let { listener ->
                    listener(song)
                }
            }
        }
    }
}

