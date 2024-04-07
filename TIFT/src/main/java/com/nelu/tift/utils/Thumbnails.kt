package com.nelu.tift.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.tift.config.Constant.DOWNLOAD_AUDIO_LINK
import com.nelu.tift.config.Constant.DOWNLOAD_ORIGINAL_VIDEO_LINK
import com.nelu.tift.config.Constant.DOWNLOAD_VIDEO_WITHOUT_WATERMARK
import com.nelu.tift.config.Constant.DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD
import com.nelu.tift.data.model.Author
import com.nelu.tift.data.model.ModelTiktok
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.di.KitTIFT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import kotlin.coroutines.resume

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getProfileVideos(activity: Activity, profile: String): List<ModelTiktok> {
    return suspendCancellableCoroutine { continuation ->
        activity.runOnUiThread {
            WebView(activity).let { webView ->
                webView.layoutParams = ViewGroup.LayoutParams(3000, 20000)

                webView.webChromeClient = object : WebChromeClient() {
                    private var isPageLoaded = false
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        if (newProgress == 100 && !isPageLoaded) {
                            isPageLoaded = true
                            // css-wjuodt-DivVideoFeedV2 ecyq5ls0
                            // css-x6y88p-DivItemContainerV2 e19c29qe8
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(3000)
                                view?.evaluateJavascript("""
                                    (function() {
                                        var button = document.querySelector('.css-u3m0da-DivBoxContainer');
                                        if (button) {
                                            button.click();
                                        } else {
                                            console.error('Button not found');
                                        }
                                    })();
                                """, null)

                                var scroll = 0
                                while (scroll < 2) {
                                    paginateWebView(view)
                                    delay(2000)
                                    scroll++
                                }

                                delay(3000)
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        var elements = document.querySelectorAll('.css-x6y88p-DivItemContainerV2');
                                        var data = [];
                                        elements.forEach(function(element) {
                                            var aTag = element.querySelector('a'); // Assuming there's only one aTag for each element
                                            var alt = aTag.querySelector('img').getAttribute('alt'); // Extracting alt attribute
                                            var href = aTag.getAttribute('href');
                                            var sourceTag = element.querySelector('source');
                                            var src = sourceTag ? sourceTag.getAttribute('src') : null;
                                            var obj = { 'alt': alt, 'href': href, 'src': src };
                                            data.push(obj);
                                        });
                                        return JSON.stringify(data);
                                    })();
                                    """.trimIndent()
                                ) { html ->
                                    val final = html.replace("\\","")
                                    val arrayData = JSONArray(final.substring(1, final.length-1))

                                    val list = mutableListOf<ModelTiktok>()

                                    // val type: String,
                                    //    val author: Author,
                                    //    val desc: String,
                                    //    val music: String,
                                    //    val videoSD: String,
                                    //    val videoHD: String?,
                                    //    val videWatermark: String
                                    for (x in 0 until arrayData.length()) {
                                        val obj = arrayData.getJSONObject(x)
                                        val videoID = extractVideoIdFromUrl(obj.getString("href"))
                                        list.add(
                                            ModelTiktok(
                                                type = URLTypes.TIKTOK.name,
                                                author = Author(
                                                    avatar = "",
                                                    nickname = profile
                                                ),
                                                desc = obj.getString("alt"),
                                                music = DOWNLOAD_AUDIO_LINK + "$videoID.mp3",
                                                videoSD = DOWNLOAD_VIDEO_WITHOUT_WATERMARK + "$videoID.mp4",
                                                videoHD = DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD + "$videoID.mp4",
                                                thumbnail = obj.getString("src"),
                                                videWatermark = DOWNLOAD_ORIGINAL_VIDEO_LINK + "$videoID.mp4"
                                            )
                                        )
                                    }

                                    continuation.resume(list)
                                }
                            }
                        }
                    }
                }

                val webSettings = webView.settings
                webSettings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36"
                webSettings.loadWithOverviewMode = true
                webSettings.useWideViewPort = true
                webSettings.javaScriptEnabled = true
                webSettings.domStorageEnabled = true
//
                activity.addContentView(webView, webView.layoutParams)
                webView.loadUrl("https://www.tiktok.com/@$profile")
                webView.visibility = View.INVISIBLE
            }
        }
    }
}

private fun extractVideoIdFromUrl(url: String): String? {
    val regex = Regex("/video/(\\d+)")
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.getOrNull(1)
}