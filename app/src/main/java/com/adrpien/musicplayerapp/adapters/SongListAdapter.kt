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

class SongListAdapter @Inject constructor (
    private val glide: RequestManager): BaseSongAdapter(R.layout.song_list_row){
    override var differ: AsyncListDiffer<Song> = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val playerIconImageView = findViewById<ImageView>(R.id.songRowImageView)
            val playerTitleTextView = findViewById<TextView>(R.id.songRowTitleTextView)
            val playerArtistTextView = findViewById<TextView>(R.id.songRowArtistTextView)
            playerTitleTextView.text = song.title
            playerArtistTextView.text = song.artist
            glide.load(song.coverURL).into(playerIconImageView)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}
