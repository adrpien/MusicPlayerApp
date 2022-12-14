package com.adrpien.musicplayerapp.depencyinjection

import android.content.Context
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.adapters.SongViewPagerAdapter
import com.adrpien.musicplayerapp.exoplayer.MusicServiceConnection
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSongViewPagerAdapter() = SongViewPagerAdapter()

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideGlideInstance(
         @ApplicationContext context: Context,
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.default_profile_icon)
            .error(R.drawable.default_profile_icon)
            .diskCacheStrategy(DiskCacheStrategy.DATA))  // setting cache strategy

}