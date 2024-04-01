package com.nelu.tift.data.model

sealed class DownloadStatus {

    data class Progress(val progress: Int) : DownloadStatus()

    object Completed : DownloadStatus()

    data class Error(val message: String) : DownloadStatus()
}