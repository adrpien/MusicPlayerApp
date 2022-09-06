package com.adrpien.musicplayerapp

import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import com.adrpien.musicplayerapp.PlayerService.PlayerServiceBinder
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding


class PlayerFragment : Fragment() {

    var playerServiceBound = false
    private lateinit var playerService: PlayerService

    private lateinit var playerServiceIntent: Intent

    private var isPlaying: Boolean = false

    // Animation lazy initalization
    private val rotateAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.player_button_rotation) }

    // Binding
    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // playerServiceIntent
        playerServiceIntent = Intent(activity , PlayerService::class.java)
        playerServiceIntent.putExtra("song", R.raw.taco_hemingway_europa)

        // Starting service
        requireActivity().startService(playerServiceIntent)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlayerServiceBinder
                playerService = binder.getService()
                playerServiceBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                playerServiceBound = false
            }

        }
        requireActivity().bindService(playerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // playerServiceIntent
        playerServiceIntent = Intent(activity , PlayerService::class.java)
        playerServiceIntent.putExtra("song", R.raw.taco_hemingway_europa)

        // Starting service
        requireActivity().startService(playerServiceIntent)

        binding.playerPlayButton.setOnClickListener {
            if(isPlaying){

                // Pending intent with broadcast for custom view play button on click
                val playIntent = Intent(getString(R.string.PLAY_ACTION))
                playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val backPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.pause_button, null))

            } else {
                // Pending intent with broadcast for custom view pause button on click
                val pauseIntent = Intent(getString(R.string.PAUSE_ACTION))
                pauseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val backPendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, null))

                // TODO Start animation
                //binding.playerPlayButton.startAnimation(rotateAnimation)
            }
        }

        // TODO Time bar implementation
        /*
        Timer implementation
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
        */
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}