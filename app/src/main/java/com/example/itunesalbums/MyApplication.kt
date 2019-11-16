package com.example.itunesalbums

import android.app.Application
import com.example.itunesalbums.logging.DebugTree
import com.example.itunesalbums.logging.FileTree
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        Timber.plant(FileTree())

    }
}
