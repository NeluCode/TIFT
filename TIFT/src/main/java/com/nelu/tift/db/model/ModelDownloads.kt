package com.nelu.tift.db.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nelu.tift.data.model.URLTypes

@Keep
@Entity
data class ModelDownloads(
    @PrimaryKey
    val id: Long,
    val name: String,
    val path: String,
    val description: String,
    val Type: String
)
