package com.adrpien.musicplayerapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.adapters.SongViewPagerAdapter
import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.databinding.ActivityMainBinding
import com.adrpien.musicplayerapp.exoplayer.toSong
import com.adrpien.musicplayerapp.other.Resource
import com.adrpien.musicplayerapp.other.ResourceState
import com.adrpien.musicplayerapp.ui.viewmodels.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting bottomNaviationMenu
        binding.bottomNavigationMenu.setupWithNavController(findNavController(R.id.fragmentContainerView))

         binding.songViewPager.adapter = songViewPagerAdapter

        subscribeToObservers()
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
    }
}