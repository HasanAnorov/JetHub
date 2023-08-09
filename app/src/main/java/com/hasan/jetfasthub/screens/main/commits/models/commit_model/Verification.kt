package com.hasan.jetfasthub.screens.main.commits.models.commit_model

data class Verification(
    val payload: Any,
    val reason: String,
    val signature: Any,
    val verified: Boolean
)