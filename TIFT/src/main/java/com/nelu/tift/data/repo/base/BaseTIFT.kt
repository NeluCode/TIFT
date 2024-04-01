package com.nelu.tift.data.repo.base

import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.URLTypes
import java.io.File

interface BaseTIFT {

    fun getDownloads(): List<File>

    suspend fun getTiktok(): BaseTiktok

    suspend fun identifyURL(url: String): URLTypes
}