package com.nelu.tift.data.repo

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nelu.tift.config.Scrapper.checkProgress
import com.nelu.tift.config.Scrapper.getVideoInfo
import com.nelu.tift.config.Scrapper.getVideoPasteFunc
import com.nelu.tift.data.model.DownloadStatus
import com.nelu.tift.data.model.Author
import com.nelu.tift.data.model.remote.ModelRequest
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.ModelTiktok.Companion.toModelTiktok
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.data.repo.base.BaseTiktok
import com.nelu.tift.data.model.local.ModelDownloads
import com.nelu.tift.di.Initializations.apiService
import com.nelu.tift.di.Initializations.daoDownloads
import com.nelu.tift.di.KitTIFT
import com.nelu.tift.utils.getProfileVideos
import com.nelu.tift.utils.processPageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume

import kotlinx.coroutines.*
import org.json.JSONException

class RepoTiktok: BaseTiktok {

    private var thumGen: WebView? = null

    override suspend fun getVideo(url: String): ModelTiktok? {
        apiService.getTiktokList(ModelRequest(url)).execute().let {
            if (it.isSuccessful)
                return it.body()!!.toModelTiktok()
        }
        return null
    }

    override suspend fun getThumbnail(activity: Activity, videoUrl: String): String {
        Log.e("VIDEO", videoUrl)
        return suspendCancellableCoroutine { continuation->
            activity.runOnUiThread {
                if (thumGen != null) {
                    thumGen?.evaluateJavascript(
                        "(function() { " +
                                "document.getElementById('link').value ='" + videoUrl + "';" +
                                "document.getElementById('make').click();" +
                                "})();"
                    ) {
                        var thumb = ""
                        CoroutineScope(Dispatchers.Main).launch {
                            while (thumb.isEmpty()) {
                                delay(500)
                                thumGen?.evaluateJavascript(
                                    """
                                    (function() {
                                        var content = {};
                                        var divContent = document.querySelector('.css-wjuodt-DivVideoFeedV2');
                                        if (divContent) {
                                            return divContent.getAttribute('src');
                                        } else {
                                            return null;
                                        }
                                    })();
                                """.trimIndent()
                                ) {
                                    thumb = it
                                }
                            }
                            continuation.resume(thumb)
                        }
                    }
                } else {
                    Log.e("From", "New")
                    WebView(KitTIFT.application).let { w ->
                        w.layoutParams = ViewGroup.LayoutParams(1, 1)
                        val s = w.settings

                        s.javaScriptEnabled = true

                        s.domStorageEnabled = true

                        w.webChromeClient = object : WebChromeClient() {
                            var sent = false
                            private var isPageLoaded = false
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                                try {
                                    JSONObject(consoleMessage.message().toString()).getString("thumbnail_url").let {
                                        continuation.resume(it)
                                    }
                                } catch (e: JSONException) {
                                    Log.d("CONSOLE EXC", e.toString())
                                } catch (e: Exception) {
                                    Log.d("CONSOLE EXC", e.toString())
                                }
                                return true
                            }
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                if (newProgress == 100 && !isPageLoaded) {
//                                    thumGen = view
                                    isPageLoaded = true
                                    w.evaluateJavascript(
                                        "(function() { " +
                                                "document.getElementById('link').value ='" + videoUrl + "';" +
                                                "document.getElementById('make').click();" +
                                                "})();", null
                                    )
                                }
                            }
                        }

                        activity.addContentView(w, w.layoutParams)
                        w.loadUrl("file:///android_asset/index.html")
                        w.visibility = View.GONE
                    }
                }
            }
        }
    }

    override suspend fun getThumbnail(
        activity: Activity,
        videoUrl: ArrayList<String>
    ): List<String> {
        val list = ArrayList<String>()
        withContext(Dispatchers.Main) {
            val sublists: List<List<String>> = videoUrl.chunked(15)
            sublists.forEach {
                list.addAll(it.map { async { getThumbnail(activity, it) } }.awaitAll())
            }
//            list.addAll(videoUrl.map { async { getThumbnail(activity, it) } }.awaitAll())
        }
        return list
    }

    override suspend fun getBatchVideo(activity: Activity, profileID: String): List<ModelTiktok> {
        return getProfileVideos(activity, profileID)
//        return suspendCancellableCoroutine { continuation ->
//            activity.runOnUiThread {
//                WebView(KitTIFT.application).let { webView ->
//
//                    webView.layoutParams = ViewGroup.LayoutParams(1, 1)
//
//                    webView.webChromeClient = object : WebChromeClient() {
//                        private var isPageLoaded = false
//                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                            super.onProgressChanged(view, newProgress)
//                            Log.e("PROGRESS", newProgress.toString())
//                            if (newProgress == 100 && !isPageLoaded) {
//                                isPageLoaded = true
//                                processPageData(view, profileID, continuation)
//                            }
//                        }
//                    }
//
//                    val webSettings = webView.settings
//
//                    webSettings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36"
//
//                    webSettings.loadWithOverviewMode = true
//
//                    webSettings.useWideViewPort = true
//
//                    webSettings.javaScriptEnabled = true
//
//                    webSettings.domStorageEnabled = true
////
//                    activity.addContentView(webView, webView.layoutParams)
//                    webView.loadUrl("https://www.tiktok.com/@$profileID")
//                    webView.visibility = View.GONE
//                }
//            }
//        }
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

    override suspend fun getTiktokInfos(
        activity: Activity,
        urls: ArrayList<String>
    ): List<ModelTiktok> {

        return suspendCancellableCoroutine {
            CoroutineScope(Dispatchers.IO).launch {
                it.resume(
                    urls.map { url ->
                        async { getVideoInfoByUrl(activity, url) }
                    }.awaitAll()
                )
            }
        }
    }

    private suspend fun getVideoInfoByUrl(activity: Activity, ids: String): ModelTiktok {
        return suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                WebView(KitTIFT.application).let { webView ->
                    webView.layoutParams = ViewGroup.LayoutParams(1, 1)

                    webView.webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                view?.evaluateJavascript(getVideoPasteFunc(ids, ids)) {}
                            }, 1000)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            CoroutineScope(Dispatchers.Main).launch {
                                var loop = true
                                while (loop) {
                                    view?.evaluateJavascript(checkProgress) {
                                        loop = it != "null"
                                    }
                                    if (loop) delay(1000)
                                }
                                view?.evaluateJavascript(getVideoInfo) { html ->
                                    JSONObject(
                                        html.substring(1, html.length-1)
                                            .replace("\\", "")
                                    ).let {
                                        continuation.resume(
                                            ModelTiktok(
                                                type = "",
                                                author = Author(
                                                    avatar = it.getString("image"),
                                                    nickname = it.getString("title")
                                                ),
                                                desc = it.getString("description"),
                                                music = "",
                                                videoSD = "",
                                                videoHD = "",
                                                thumbnail = "",
                                                videWatermark = ""
                                            )
                                        )
                                    }
                                }

                            }
                        }
                    }

                    val webSettings = webView.settings
                    webSettings.javaScriptEnabled = true
                    webSettings.domStorageEnabled = true

                    activity.addContentView(webView, webView.layoutParams)
                    webView.loadUrl("https://savetik.net/")
                    webView.visibility = View.GONE
                }
            }
        }
    }

    private fun extractNumberFromUrl(url: String): String {
        val regex = Regex("""(\d+)\.mp3""")
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value ?: "${System.currentTimeMillis()}"
    }
}