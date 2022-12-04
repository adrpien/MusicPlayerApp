package com.adrpien.musicplayerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.data.entities.Song
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class PlaylistListAdapter @Inject constructor() : RecyclerView.Adapter<PlaylistListAdapter.PlaylistViewHolder>(){

    // Differ Util implementation
    protected val differCallback = object: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    protected val differ: AsyncListDiffer<String> = AsyncListDiffer(this, differCallback)


    // List of playlists
    var playlists: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    // ViewHolder class
    inner class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutInflater.
            from(parent.context).
            inflate(R.layout.playlist_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        var playlist = playlists[position]
        holder.itemView.apply {
            val playlistRowTextView = findViewById<TextView>(R.id.playlistRowTextView)
            playlistRowTextView.text = playlist
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}
