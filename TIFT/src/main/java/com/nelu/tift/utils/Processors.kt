package com.nelu.tift.utils

import android.util.Log
import android.webkit.WebView
import com.nelu.tift.data.model.ModelTiktok
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
fun processPageData(view: WebView?, continuation: CancellableContinuation<List<String>>) {

    var retried = 0
    val videoIds = mutableListOf<String>()

    CoroutineScope(Dispatchers.Main).launch {
        while (videoIds.isEmpty() && retried < 10) {
            delay(1000)
            view?.evaluateJavascript(
                "(function() { return document.documentElement.outerHTML; })();"
            ) { html ->
                retried++
                val urlRegex = Regex("""https://www\.tiktok\.com/@\w+/video/(\d+)""")
                val matches = urlRegex.findAll(html)

                for (match in matches) {
                    val videoId = match.groupValues[1]
                    videoIds.add(videoId)
                }
            }
        }

        continuation.resume(videoIds) { e ->
            view?.stopLoading()
        }
    }
}