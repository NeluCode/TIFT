package com.nelu.tift.data.repo

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.tift.data.model.DownloadStatus
import com.nelu.tift.data.apis.ApiService
import com.nelu.tift.data.model.remote.ModelRequest
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.ModelTiktok.Companion.toModelTiktok
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.data.repo.base.BaseTiktok
import com.nelu.tift.db.dao.DaoDownloads
import com.nelu.tift.data.model.local.ModelDownloads
import com.nelu.tift.di.Initializations.apiService
import com.nelu.tift.di.Initializations.daoDownloads
import com.nelu.tift.di.KitTIFT
import com.nelu.tift.utils.processPageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File

class RepoTiktok: BaseTiktok {

    override suspend fun getVideo(url: String): ModelTiktok? {
        apiService.getTiktokList(ModelRequest(url)).execute().let {
            if (it.isSuccessful)
                return it.body()!!.toModelTiktok()
        }
        return null
    }

    override suspend fun getBatchVideo(activity: Activity, profileID: String): List<String> {
        return suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                WebView(KitTIFT.application).let { webView ->
                    webView.layoutParams = ViewGroup.LayoutParams(1, 1)

                    webView.webChromeClient = object : WebChromeClient() {
                        private var isPageLoaded = false
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            Log.e("PROGRESS", newProgress.toString())
                            if (newProgress == 100 && !isPageLoaded) {
                                isPageLoaded = true
                                processPageData(view, continuation)
                            }
                        }
                    }

                    val webSettings = webView.settings
                    webSettings.javaScriptEnabled = true
                    webSettings.domStorageEnabled = true

                    activity.addContentView(webView, webView.layoutParams)
                    webView.loadUrl("https://www.tiktok.com/@$profileID")
                    webView.visibility = View.GONE
                }
            }
        }
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
        val outputFile = File(KitTIFT.application.filesDir, name)

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

                daoDownloads.insertDownloads(
                    ModelDownloads(
                        id = System.currentTimeMillis(),
                        name = data.author.nickname,
                        path = outputFile.absolutePath,
                        description = data.desc,
                        type = URLTypes.TIKTOK
                    )
                )

                emit(DownloadStatus.Completed)
            }
        }
    }

    override fun downloadTiktoks(data: List<Pair<ModelTiktok, String>>): Flow<DownloadStatus> {
        TODO("Not yet implemented")
    }

    private fun extractNumberFromUrl(url: String): String {
        val regex = Regex("""(\d+)\.mp3""")
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value ?: "${System.currentTimeMillis()}"
    }
}