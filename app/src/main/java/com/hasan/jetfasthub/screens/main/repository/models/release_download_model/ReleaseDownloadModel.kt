package com.hasan.jetfasthub.screens.main.repository.models.release_download_model

data class ReleaseDownloadModel(
    val title: String,
    val url: String,
    val extension: String,
    val downloadCount: Int,
    val notificationTitle: String
)