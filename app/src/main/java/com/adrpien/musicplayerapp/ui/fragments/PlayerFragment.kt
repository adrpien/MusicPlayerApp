package com.adrpien.musicplayerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.adrpien.musicplayerapp.adapters.SongListAdapter
import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import com.adrpien.musicplayerapp.exoplayer.toSong
import com.adrpien.musicplayerapp.other.ResourceState
import com.adrpien.musicplayerapp.ui.viewmodels.MainViewModel
import com.adrpien.musicplayerapp.ui.viewmodels.PlayerViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    // Binding
    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var glide: RequestManager

    // ViewModels
    private lateinit var mainViewModel: MainViewModel
    private  lateinit var playerViewModel: PlayerViewModel

    @Inject
    lateinit var songListAdapter: SongListAdapter

    private var currentlyPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting reference to PlayerViewModel
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Getting reference to MainViewModel
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        subscribeToObservers()

        //binding.

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Updates all component which shows song data
    private fun updateSongDataInLayout(song: Song){
        binding.playerTitleTextView.text = song.title
        binding.playerArtistTextView.text = song.artist
        glide.load(song.coverURL.toUri()).into(binding.playerIconImageView)
    }

    // Observing ViewModel
    private fun subscribeToObservers(){

        // We do observe this media items in order to get song list and set first element of list as currentlyPlayingSong
        // This is useful in case when we loaded list, but did not played any song yet.
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.resourceState) {
                ResourceState.SUCCESS -> {
                    result.data?.let { songList ->
                        if (currentlyPlayingSong == null && songList.isNotEmpty()) {
                            currentlyPlayingSong = songList[0]
                            updateSongDataInLayout(songList[0])
                        }
                    }
                } else  -> Unit
            }
        }

        mainViewModel.currentlyPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            currentlyPlayingSong = it?.toSong()
            updateSongDataInLayout(currentlyPlayingSong!!)
        }
    }

}