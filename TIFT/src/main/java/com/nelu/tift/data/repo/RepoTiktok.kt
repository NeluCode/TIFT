package com.nelu.tift.data.repo

import com.nelu.tift.data.model.DownloadStatus
import com.nelu.tift.data.apis.ApiService
import com.nelu.tift.data.apis.ModelRequest
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.ModelTiktok.Companion.toModelTiktok
import com.nelu.tift.data.repo.base.BaseTiktok
import com.nelu.tift.di.KitTIFT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class RepoTiktok(
    private val apiService: ApiService
): BaseTiktok {


    override suspend fun getVideo(url: String): ModelTiktok? {
        apiService.getTiktokList(ModelRequest(url)).execute().let {
            if (it.isSuccessful)
                return it.body()!!.toModelTiktok()
        }
        return null
    }

    override fun downloadTiktok(data: ModelTiktok, url: String): Flow<DownloadStatus> = flow {
        val name = extractNumberFromUrl(data.music) + ".mp4"
        val response = apiService.downloadFile(url).execute()
        val body = response.body()

        if (!response.isSuccessful || body == null) {
            emit(DownloadStatus.Error("Download failed with code: ${response.code()}"))
            return@flow
        }

        val totalFileSize = body.contentLength()
        val outputFile = File(KitTIFT.application.externalCacheDir, name)

        if (outputFile.exists()) outputFile.deleteRecursively()

        var fileSizeDownloaded: Long = 0

        emit(DownloadStatus.Progress(0))

        body.byteStream().use { inputStream ->
            outputFile.outputStream().use { outputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    fileSizeDownloaded += bytesRead

                    val progress = ((fileSizeDownloaded * 100) / totalFileSize).toInt()
                    emit(DownloadStatus.Progress(progress))
                }

                emit(DownloadStatus.Completed)
            }
        }
    }

    private fun extractNumberFromUrl(url: String): String {
        val regex = Regex("""(\d+)\.mp3""")
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value ?: "${System.currentTimeMillis()}"
    }
}