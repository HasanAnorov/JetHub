package com.hasan.jetfasthub.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.hasan.jetfasthub.screens.main.repository.models.release_download_model.ReleaseDownloadModel
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN


interface Downloader {

    fun downloadCommit(url: String, message: String): Long

    fun downloadRepo(url: String, message: String): Long

    fun downloadRelease(release: ReleaseDownloadModel): Long

    fun downloadFile(url: String, message: String): Long

}

class AndroidDownloader(context: Context) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadCommit(url: String, message: String): Long {
        val mimeType = getMimeType(url) ?: "application/octet-stream"
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(message)
            .addRequestHeader("Authorization", "Bearer $PERSONAL_ACCESS_TOKEN")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                message
            )

        return downloadManager.enqueue(request)
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    override fun downloadFile(url: String, message: String): Long {
        val mimeType = getMimeType(url) ?: ""
        Log.d("ahi3646", "downloadFile: $mimeType")
        val request = DownloadManager.Request(Uri.parse(url))
            .setMimeType(mimeType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(message)
            .addRequestHeader("Authorization", "Bearer $PERSONAL_ACCESS_TOKEN")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                message
            )

        return downloadManager.enqueue(request)
    }

    override fun downloadRepo(url: String, message: String): Long {
        val mimeType = getMimeType(url) ?: "application/zip"
        Log.d("ahi3646", "downloadRepo: $mimeType ")
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(message)
            .addRequestHeader("Authorization", "Bearer $PERSONAL_ACCESS_TOKEN")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                message
            )

        return downloadManager.enqueue(request)
    }

    override fun downloadRelease(release: ReleaseDownloadModel): Long {
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