package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import com.hasan.jetfasthub.utility.ParseDateFormat
import retrofit2.Response

interface NotificationRepository {
    suspend fun getAllNotifications(token: String): Response<Notification>

    suspend fun getUnreadNotifications(token: String, since: String): Response<Notification>

    suspend fun markAsRead(token: String, threadId: String): Response<Int>
}

class NotificationsRepositoryImpl(private val context: Context) : NotificationRepository {

    override suspend fun getAllNotifications(token: String): Response<Notification> {
        return RetrofitInstance(context = context).gitHubService.getAllNotifications(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            perPage = 50,
            all = true
        )
    }

    override suspend fun getUnreadNotifications(
        token: String,
        since: String
    ): Response<Notification> {
        return RetrofitInstance(context).gitHubService.getUnreadNotifications(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            since = ParseDateFormat.lastWeekDate
        )
    }

    override suspend fun markAsRead(token: String, threadId: String): Response<Int> {
        return RetrofitInstance(context).gitHubService.markAsRead(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            threadId
        )
    }

}