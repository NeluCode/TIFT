package com.nelu.tift.data.repo.base

import androidx.annotation.Keep
import com.nelu.tift.data.model.DownloadStatus
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.db.dao.DaoDownloads
import kotlinx.coroutines.flow.Flow

@Keep
interface BaseTiktok {

    suspend fun getVideo(url: String): ModelTiktok?

    fun downloadTiktok(data: ModelTiktok, url: String): Flow<DownloadStatus>
}