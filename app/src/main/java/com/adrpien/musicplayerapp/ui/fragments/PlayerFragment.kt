package com.adrpien.musicplayerapp.ui.fragments

import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.adapters.SongListAdapter
import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import com.adrpien.musicplayerapp.exoplayer.MusicServiceConnection
import com.adrpien.musicplayerapp.exoplayer.isPlaying
import com.adrpien.musicplayerapp.exoplayer.toSong
import com.adrpien.musicplayerapp.other.ResourceState
import com.adrpien.musicplayerapp.ui.viewmodels.MainViewModel
import com.adrpien.musicplayerapp.ui.viewmodels.PlayerViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
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
    lateinit var musicServiceConnection: MusicServiceConnection

    @Inject
    lateinit var songListAdapter: SongListAdapter

    private var currentlyPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true

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

        // Listeners for buttons in fragment
        binding.playerPlayPauseButton.setOnClickListener {
            currentlyPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        binding.playerNextButton.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        binding.playerPreviousButton.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        // Setting up playerSongTimeSeekBar
        binding.playerSongTimeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, position: Int, fromUser: Boolean) {

                // When seekbar is dragged change the playerSongCurrentTimeTextView, do not change current song time
                if (fromUser) {
                    convertSongMomentToString(position.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            // Change current song time when dragging is endedf
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }

            }

        })
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

        // Need this to change image of playerPlayPauseButton when playbackState changed
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.playerPlayPauseButton.setImageResource(
                if (playbackState?.isPlaying == true){
                    R.drawable.pause_button
                } else {
                    R.drawable.play_button
                }
            )

            binding.playerSongTimeSeekBar.progress = it?.position?.toInt() ?: 0
        }

        //  playerViewModel Used for Seekbar
        playerViewModel.songCurrentMillisecond.observe(viewLifecycleOwner) {
            binding.playerSongTimeSeekBar.progress = it.toInt()
            if(shouldUpdateSeekbar) {
                binding.playerSongCurrentTimeTextView.text == convertSongMomentToString(it)
            }
        }

        playerViewModel.currentSongDuration.observe(viewLifecycleOwner) {
            binding.playerSongTimeSeekBar.max = it.toInt()
            binding.playerSongDurationTimeTextView.text = convertSongMomentToString(it)
        }
    }

    // This function make conversion of current exact moment of song into String like "00:00"
    private fun convertSongMomentToString(millis: Long): String {
        var dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(millis)
    }

}