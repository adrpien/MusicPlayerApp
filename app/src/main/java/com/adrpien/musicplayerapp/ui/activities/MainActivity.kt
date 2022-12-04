package com.adrpien.musicplayerapp.ui.activities

import android.media.session.PlaybackState
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2.INVISIBLE
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.adapters.SongViewPagerAdapter
import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.databinding.ActivityMainBinding
import com.adrpien.musicplayerapp.exoplayer.isPlaying
import com.adrpien.musicplayerapp.exoplayer.toSong
import com.adrpien.musicplayerapp.other.ResourceState
import com.adrpien.musicplayerapp.ui.viewmodels.MainViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var songViewPagerAdapter: SongViewPagerAdapter

    // By declaring view model this way, we bind our activity lifecycle to our view model
    // When we want to declare our lifecycle owner explicitly we net to use viewModelProvider and point lifecycle owner
    private val mainViewModel: MainViewModel by viewModels()

    private var currentlyPlayingSong: Song?  = null
    private var playbackState: PlaybackStateCompat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hiding SupportActionBar
        supportActionBar?.hide()

        // Setting bottomNaviationMenu
        binding.bottomNavigationMenu.setupWithNavController(findNavController(R.id.fragmentContainerView))

        // Trigger this lambda when NacController changes its destination
        findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener(object: NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when(destination.id) {
                    R.id.playerFragment -> {
                     hideSongBarLayout()
                    }
                    R.id.playlistFragment -> {
                        showSongBarLayout()
                    }
                    R.id.homeFragment -> {
                        showSongBarLayout()
                    }
                    else -> Unit
                }
            }
        })


        binding.songViewPager.adapter = songViewPagerAdapter
        binding.songViewPager.registerOnPageChangeCallback(object: OnPageChangeCallback() {

            // This function is triggered everytime the item changes
            override fun onPageSelected(position: Int) {
                if(playbackState?.isPlaying == true){
                    mainViewModel.playOrToggleSong(songViewPagerAdapter.songs[position])
                } else {
                    currentlyPlayingSong = songViewPagerAdapter.songs[position]
                }
            }
        })
        songViewPagerAdapter.setItemClickListener { song ->
            findNavController(R.id.fragmentContainerView).navigate(R.id.globalActionToPlayerFragment)
        }


        subscribeToObservers()


        binding.songPlaybackStateButton.setOnClickListener { view ->
            currentlyPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
    }


    // Function to hide songBarLayout
    private fun hideSongBarLayout(){
        binding.songBarLayout.isVisible = false
    }

    // Function to sho w  songBarLayout
    private fun showSongBarLayout(){
        binding.songBarLayout.isVisible = true
    }

    // Function which switches View Pager to current song
    private fun updateViewPager(song: Song){
        val songIndex = songViewPagerAdapter.songs.indexOf(song)
        if (songIndex != -1) {
            binding.songViewPager.currentItem = songIndex
            currentlyPlayingSong = song

        }
    }

    // Function to observe LiveData
    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.resourceState) {
                    ResourceState.SUCCESS -> {
                        result.data?.let { songs ->
                             songViewPagerAdapter.songs = songs
                            if(songs.isNotEmpty()) {
                                glide.load((currentlyPlayingSong ?: songs[0]).coverURL).into(binding.songImageView)
                            }
                            updateViewPager((currentlyPlayingSong) ?: return@observe)

                        }
                    }
                    ResourceState.LOADING -> Unit
                    ResourceState.ERROR -> Unit
                }
            }
        }

        mainViewModel.currentlyPlayingSong.observe(this) {
            if(it == null) return@observe
            currentlyPlayingSong = it.toSong()
            glide.load(currentlyPlayingSong?.coverURL).into(binding.songImageView)
            updateViewPager((currentlyPlayingSong) ?: return@observe)
        }
        mainViewModel.playbackState.observe(this) {
            playbackState = it
                binding.songPlaybackStateButton.setImageResource(
                    if(playbackState?.isPlaying == true) R.drawable.pause_button
                    else R.drawable.play_button
                )

        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when(result.resourceState) {
                    ResourceState.ERROR -> {
                        Snackbar.make(
                            binding.rootLayout,
                            result.message ?: "Error occured",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when(result.resourceState) {
                    ResourceState.ERROR -> {
                        Snackbar.make(
                            binding.rootLayout,
                            result.message ?: "Error occured",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}