package com.nelu.tift.di

import android.app.Application
import androidx.annotation.Keep
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.data.model.local.ModelDownloads
import com.nelu.tift.data.repo.base.BaseTIFT
import com.nelu.tift.data.repo.base.BaseTiktok
import kotlinx.coroutines.Dispatchers

@Keep
object KitTIFT : BaseTIFT {

    /** Required variables to hold single instance of TIFT */
    private lateinit var tift: BaseTIFT

    lateinit var application: Application

    /** Initializer (Compulsory) */
    fun get(application: Application): BaseTIFT {
        KitTIFT.application = application
        check(KitTIFT::application.isInitialized) {
            "KitTIFT not initialized"
        }
        if (!KitTIFT::tift.isInitialized) tift = TIFT().getTIFT()
        return tift
    }

    /** TIFT accessor */
    override val tiktok: BaseTiktok get() = tift.tiktok

    override suspend fun identifyURL(url: String) : URLTypes = tift.identifyURL(url)

    override suspend fun getDownloads() : List<ModelDownloads> = tift.getDownloads()
}