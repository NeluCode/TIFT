package com.nelu.tift.data.model

import androidx.annotation.Keep


@Keep
sealed class DownloadStatus {

    @Keep
    data class Progress(val progress: Int) : DownloadStatus()

    @Keep
    object Completed : DownloadStatus()

    @Keep
    data class Error(val message: String) : DownloadStatus()
}