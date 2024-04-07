package com.nelu.tift.data.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.nelu.tift.data.model.Author.Companion.toAuthor

@Keep
data class ModelTiktok(
    val type: String,
    val author: Author,
    val desc: String,
    val music: String,
    val videoSD: String,
    val videoHD: String?,
    val thumbnail: String,
    val videWatermark: String
) {

    companion object {
        fun JsonObject.toModelTiktok(): ModelTiktok {
            get("data").asJsonObject.get("result").asJsonObject.run {
                return ModelTiktok(
                    get("type").asString,
                    get("author").asJsonObject.toAuthor(),
                    get("desc").asString,
                    get("music").asString,
                    get("video1").asString,
                    get("video_hd")?.asString,
                    "",
                    get("video_watermark").asString
                )
            }
        }
    }
}


@Keep
data class Author(
    val avatar: String,
    val nickname: String,
) {
    companion object {
        fun JsonObject.toAuthor(): Author {
            return Author(
                get("avatar").asString,
                get("nickname").asString
            )
        }
    }
}