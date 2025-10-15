package com.example.proyecto.models

import android.app.Application
import android.util.Log
import com.example.proyecto.utilities.TokenManager
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback

class MyApp : Application(), OnMapsSdkInitializedCallback {
    companion object {
        lateinit var tokenManager: TokenManager
    }

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)

        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MyApp", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MyApp", "The legacy version of the renderer is used.")
        }
    }
}