package com.nelu.tift.data.repo.base

import androidx.annotation.Keep
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.db.model.ModelDownloads
import java.io.File

@Keep
interface BaseTIFT {

    suspend fun getTiktok(): BaseTiktok

    suspend fun identifyURL(url: String): URLTypes

    suspend fun getDownloads(): List<ModelDownloads>
}