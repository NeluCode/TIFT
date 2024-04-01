package com.nelu.tiftpreview

import android.app.Application
import com.nelu.tift.di.KitTIFT

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        KitTIFT.get(this)
    }
}