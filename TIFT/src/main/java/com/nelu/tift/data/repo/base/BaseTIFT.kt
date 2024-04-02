package com.nelu.tift.data.repo.base

import androidx.annotation.Keep
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.data.model.local.ModelDownloads

@Keep
interface BaseTIFT {

    val tiktok: BaseTiktok

    suspend fun identifyURL(url: String): URLTypes

    suspend fun getDownloads(): List<ModelDownloads>
}