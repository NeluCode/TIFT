package com.nelu.tift.data.model

import androidx.annotation.Keep


@Keep
sealed class DownloadStatus {

    data class Progress(val progress: Int) : DownloadStatus()

    object Completed : DownloadStatus()

    data class Error(val message: String) : DownloadStatus()
}