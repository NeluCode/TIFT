package com.nelu.tift.data.repo.base

import com.nelu.tift.data.model.DownloadStatus
import com.nelu.tift.data.model.ModelTiktok
import kotlinx.coroutines.flow.Flow

interface BaseTiktok {

    suspend fun getVideo(url: String): ModelTiktok?

    fun downloadTiktok(data: ModelTiktok, url: String): Flow<DownloadStatus>
}