package com.hasan.jetfasthub.screens.main.repository.models.repo_subscription_model

data class RepoSubscriptionModel(
    val created_at: String,
    val ignored: Boolean,
    val reason: Any,
    val repository_url: String,
    val subscribed: Boolean,
    val url: String
)