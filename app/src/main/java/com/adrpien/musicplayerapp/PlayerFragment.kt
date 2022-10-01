package com.adrpien.musicplayerapp

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
import com.adrpien.musicplayerapp.PlayerService.PlayerServiceBinder
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import kotlinx.coroutines.currentCoroutineContext
import java.util.concurrent.TimeUnit


class PlayerFragment : Fragment() {

    private lateinit var songList: MutableList<Song>


      var playerServiceBound = false
    private lateinit var playerService: PlayerService
    private lateinit var serviceConnection: ServiceConnection
    private lateinit var playerServiceIntent: Intent

    // Animation lazy initalization
    private val rotateAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.player_button_rotation) }

    // Binding
    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Starting Player Service
        startPlayerService()
        songList = getSongList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // playerServiceIntent
        playerServiceIntent = Intent(activity , PlayerService::class.java)
        playerServiceIntent.putExtra("song", songList[1].uri)

        // Starting service
        requireActivity().startService(playerServiceIntent)

        // TODO playerPlayButton onClick implementation
       /* binding.playerPlayButton.setOnClickListener {
            if(!isPlaying){
                // Pending intent with broadcast for custom view play button on click
                val playIntent = Intent(getString(R.string.PLAY_ACTION))
                playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                //val backPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
                // backPendingIntent.send()
                context?.sendBroadcast(playIntent)
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.pause_button, null))
            } else {
                // Pending intent with broadcast for custom view pause button on click
                val pauseIntent = Intent(getString(R.string.PAUSE_ACTION))
                pauseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                //val backPendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
                context?.sendBroadcast(pauseIntent)
                // backPendingIntent.send()
                binding.playerPlayButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, null))
                // TODO Start animation
                //binding.playerPlayButton.startAnimation(rotateAnimation)
            }
        }*/

        // TODO Time bar implementation
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().stopService(playerServiceIntent)
        // requireActivity().unbindService(serviceConnection)
        // playerServiceBound = false
        _binding = null
    }

    private fun startPlayerService() {
        // playerServiceIntent
        playerServiceIntent = Intent(activity, PlayerService::class.java)
        playerServiceIntent.putExtra("song", R.raw.taco_hemingway_europa)

        // Starting service
        requireActivity().startService(playerServiceIntent)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlayerServiceBinder
                playerService = binder.getService()
                playerServiceBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                playerServiceBound = false
            }

        }

        // DO NOT BIND FRAGMENT TO SERVICE
        // requireActivity().bindService(playerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun getSongList(): MutableList<Song> {

        val songList = mutableListOf<Song>()

        // Getting collection
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            // MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        // Projecting data
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        // Selection from collection
        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES).toString()
        )

        // Setting collection sort order (alphabetical)
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val query = activity?.contentResolver?.query(
            collection,
            projection,
            null,
            null,
            null
        )

        query?.use{ cursor ->

            val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            if(cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumnIndex)
                    val name = cursor.getString(nameColumnIndex)
                    val artist = cursor.getString(artistColumnIndex)
                    val album = cursor.getString(albumColumnIndex)
                    val duration = cursor.getString(durationColumnIndex)
                    val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    songList += Song(contentUri, name, artist, album, duration)
                }
            }
        }
        return songList
    }

    private fun getSongUris(songList: MutableList<Song>): MutableList<Uri>{
        val songUris = mutableListOf<Uri>()
        for (item in songList){
            songUris.add(item.uri)
        }
        return songUris
    }
}