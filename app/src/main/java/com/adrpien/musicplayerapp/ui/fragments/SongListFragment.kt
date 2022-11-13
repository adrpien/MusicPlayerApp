package com.adrpien.musicplayerapp.ui.fragments

import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.CalendarContract.Attendees.query
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.query
import android.provider.MediaStore.VOLUME_EXTERNAL
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrpien.musicplayerapp.adapters.SongListAdapter
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import com.adrpien.musicplayerapp.databinding.FragmentSongListBinding
import com.adrpien.musicplayerapp.other.Resource
import com.adrpien.musicplayerapp.other.ResourceState
import com.adrpien.musicplayerapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.currentCoroutineContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint()
class SongListFragment : Fragment() {

    // Binding
    private var _binding: FragmentSongListBinding? = null
    private val binding
        get() = _binding!!

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var songListAdapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creating ViewModel this way, explicitly passes activity as LifeCycleOwner
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java )

        setupRecyclerView()
        subscribeToObservers()
        songListAdapter.setOnItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.resourceState) {
                ResourceState.SUCCESS -> {
                    binding.songListProgressBar.isVisible = false
                    result.data?.let { songs ->
                        songListAdapter.songs = songs
                    }
                }
                ResourceState.ERROR -> Unit
                ResourceState.LOADING -> binding.songListProgressBar.isVisible = true
            }
        }

    }

    private fun setupRecyclerView() = binding.songListRecyclerView.apply {
        adapter = songListAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}