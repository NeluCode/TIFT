package com.nelu.tiftpreview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.di.KitTIFT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        CoroutineScope(Dispatchers.IO).launch {
//            val url = "https://www.tiktok.com/@shamima_afrinomi338/video/7345759236976053522?is_from_webapp=1&sender_device=pc"
//            KitTIFT.identifyURL(url).let {
//                Log.e("TYPE", it.toString())
//                when(it) {
//                    URLTypes.TIKTOK -> downloadTiktok(url)
//                    URLTypes.FACEBOOK -> TODO()
//                    URLTypes.INSTAGRAM -> TODO()
//                    URLTypes.TWITTER -> TODO()
//                    URLTypes.UNKNOWN -> TODO()
//                }
//            }
//        }

        CoroutineScope(Dispatchers.IO).launch {
//            KitTIFT.tiktok.getThumbnail(this@SplashActivity, "https://www.tiktok.com/@shamima_afrinomi338/video/7345759236976053522").let {
//                Log.e("Thumbnail", it)
//            }
            val threadSafeList = CopyOnWriteArrayList<String>()
            threadSafeList.addAll(
                KitTIFT.tiktok.getBatchVideo(
                    this@SplashActivity,
                    "shamima_afrinomi338"
                )
            )

            KitTIFT.tiktok.getThumbnail(this@SplashActivity, ArrayList(threadSafeList)).let {
                Log.e("Thumbnail", it.toString())
            }
//            KitTIFT.tiktok.getTiktokInfos(
//                this@SplashActivity,
//                ArrayList<String>().also {
//                    it.add("7340174709188726034")
//                    it.add("7340174709188726034")
//                    it.add("7340174709188726034")
//                    it.add("7340174709188726034")
//                }
//            ).let {
//                Log.e("DATA", it.toString())
//            }
        }
    }

    private suspend fun downloadTiktok(url: String) {
//        KitTIFT.tiktok.getBatchVideo(this, "shamima_afrinomi338").let {
//            Log.e("Batch Video id's", it.toString())
//        }

        KitTIFT.tiktok.getVideo(url)?.let {
            KitTIFT.tiktok.downloadTiktok(it, it.videoHD ?: it.videoSD).collect {
                println(it)
            }
            println(it)
        }

        KitTIFT.getDownloads().let {
            Log.e("My Downloads", it.toString())
        }
    }
}