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
import com.adrpien.musicplayerapp.databinding.FragmentPlayerBinding
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.currentCoroutineContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

 @AndroidEntryPoint
class PlayerFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    // Binding
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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}