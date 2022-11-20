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

class SongListAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {

    // Diff util is tool which calculate differences between lists in order to make update recycler view operation more efficient
    // Instead of using notifyDataSetChanged which updates whole RecyclerView
    private val diffCallback = object: DiffUtil.ItemCallback<Song>(){

        // Checks if media ID is the same
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        // Checks if two items are exact same
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.song_list_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
    val song = songs[position]
    holder.itemView.apply {
        val playerIconImageView = findViewById<ImageView>(R.id.songRowImageView)
        val playerTitleTextView = findViewById<TextView>(R.id.songRowTitleTextView)
        val playerArtistTextView = findViewById<TextView>(R.id.songRowArtistTextView)
        playerTitleTextView.text = song.title
        playerArtistTextView.text = song.artist
        glide.load(song.coverURL).into(playerIconImageView)

        setOnItemClickListener { song ->
            onItemClickListener?.let { listener ->
                listener(song)
            }
        }
    }
    }

    override fun getItemCount(): Int {
    return  songs.size
    }

    private var onItemClickListener: ((Song) -> Unit)? = null

    fun setOnItemClickListener(listener: (Song) -> Unit){
        onItemClickListener = listener
    }

    class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
     }

}