package com.adrpien.musicplayerapp

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import java.util.*
import kotlin.concurrent.timerTask


class PlayerFragment : Fragment() {

    private var currentPosition: Long = 0
    private var isPlaying: Boolean = false
    private lateinit var mediaPlayer: MediaPlayer

    // lazy
    private val rotateAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.player_button_rotation) }

    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // playerServiceIntent
        val playerServiceIntent = Intent(activity , PlayerService::class.java)



        // Play, next, back buttons implementations
        binding.playerNextButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.next_button, null))
        binding.playerBackButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.back_button, null))
        binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, null))
        binding.playerPlayButton.setOnClickListener {
            if(isPlaying){
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, null))
                binding.playerPlayButton.startAnimation(rotateAnimation)
                requireActivity().stopService(playerServiceIntent)
                isPlaying = false
            } else {
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.pause_button, null))
                binding.playerPlayButton.startAnimation(rotateAnimation)
                requireActivity().startService(playerServiceIntent)
                isPlaying = true
            }
        }

        // TODO mediaPlayer initialization
        // Here

        // Timer implementation
        val timer = Timer()
        if(mediaPlayer != null && mediaPlayer.isPlaying){
            timer.scheduleAtFixedRate(timerTask {
                currentPosition = mediaPlayer.currentPosition.toLong()
                binding.textView.text = currentPosition.toString()
            },0,1000)
        } else {
            timer.cancel()
            timer.purge()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}