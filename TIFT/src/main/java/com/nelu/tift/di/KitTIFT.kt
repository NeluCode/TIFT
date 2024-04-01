package com.nelu.tift.di

import android.app.Application
import androidx.annotation.Keep
import com.nelu.tift.data.repo.base.BaseTIFT

@Keep
object KitTIFT {

    val INSTANCE get() = getTIFT()

    private lateinit var tift: BaseTIFT

    lateinit var application: Application

    private fun getTIFT() : BaseTIFT {
        if (KitTIFT::application.isInitialized.not())
            throw IllegalStateException("KitTIFT not initialized")
        if (!KitTIFT::tift.isInitialized)
            tift = TIFT().getTIFT()
        return tift
    }

    fun get(application: Application): BaseTIFT {
        KitTIFT.application = application
        return getTIFT()
    }
}