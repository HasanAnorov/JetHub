package com.hasan.jetfasthub.screens.main.notifications.model

data class NotificationItem(
    val id: String,
    val last_read_at: String,
    val reason: String,
    val repository: Repository,
    val subject: Subject,
    val subscription_url: String,
    val unread: Boolean,
    val updated_at: String,
    val url: String
)