package com.hasan.jetfasthub.data.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.hasan.jetfasthub.screens.main.repository.models.release_download_model.ReleaseDownloadModel
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN

interface Downloader {
    fun download(release: ReleaseDownloadModel): Long
}

class AndroidDownloader(context: Context) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun download(release: ReleaseDownloadModel): Long {
        val request = DownloadManager.Request(release.url.toUri())
            .setMimeType(release.extension)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(release.title)
            .addRequestHeader("Authorization", "Bearer $PERSONAL_ACCESS_TOKEN")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                release.notificationTitle + release.extension
            )

        return downloadManager.enqueue(request)
    }

}