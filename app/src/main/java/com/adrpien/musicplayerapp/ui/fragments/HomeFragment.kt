package com.adrpien.musicplayerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.adapters.PlaylistListAdapter
import com.adrpien.musicplayerapp.adapters.SongListAdapter
import com.adrpien.musicplayerapp.databinding.FragmentHomeBinding
import com.adrpien.musicplayerapp.databinding.FragmentSongListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var playlistList: List<String> = listOf("list1", "list2", "list3", "list4", "list5", "list6")

    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var playlistListAdapter: PlaylistListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
    }

    // Observing Livadata
    private fun subscribeToObservers() {
        // TODO HomeFragment subscribeToObservers to implement
    }

    // Setting up RecyclerView
    private fun setupRecyclerView() = binding.playlistRecyclerView.apply {
            // For testing only, waiting for viewmodel and observers implementation
            playlistListAdapter.playlists = playlistList

            layoutManager = GridLayoutManager(requireContext(),2)
             adapter = playlistListAdapter
    }
}