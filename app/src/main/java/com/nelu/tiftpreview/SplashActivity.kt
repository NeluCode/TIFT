package com.nelu.tiftpreview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.di.KitTIFT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://www.tiktok.com/@shamima_afrinomi338/video/7345759236976053522?is_from_webapp=1&sender_device=pc"
            KitTIFT.INSTANCE.identifyURL(url).let {
                Log.e("TYPE", it.toString())
                when(it) {
                    URLTypes.TIKTOK -> downloadTiktok(url)
                    URLTypes.FACEBOOK -> TODO()
                    URLTypes.INSTAGRAM -> TODO()
                    URLTypes.TWITTER -> TODO()
                    URLTypes.UNKNOWN -> TODO()
                }
            }
        }
    }

    private suspend fun downloadTiktok(url: String) {
        KitTIFT.INSTANCE.getTiktok().getVideo(url)?.let {
            KitTIFT.INSTANCE.getTiktok().downloadTiktok(it.videoSD).collect {
                println(it)
            }
            println(it)
        }
    }
}