package com.adrpien.musicplayerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.data.entities.Song
import com.bumptech.glide.RequestManager
import javax.inject.Inject

abstract class BaseSongAdapter(
    private val layoutId: Int
): RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

    // Diff util is tool which calculate differences between lists in order to make update recycler view operation more efficient
    // Use instead of using notifyDataSetChanged which updates whole RecyclerView
    protected val diffCallback = object: DiffUtil.ItemCallback<Song>(){

        // Checks if media ID is the same
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        // Checks if two items are exact same
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val  differ: AsyncListDiffer<Song>


    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutId,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return  songs.size
    }

    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit){
        onItemClickListener = listener
    }

    inner class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    }

}