package com.hasan.jetfasthub.data.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadCompletedReceiver: BroadcastReceiver() {

    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETED") {
//            write query regarding download status here
//            val query = DownloadManager.Query().setFilterByStatus()
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id != -1L){
                Log.d("ahi3646 download", "onReceive: Download with $id finished!")
            }
        }
    }

}