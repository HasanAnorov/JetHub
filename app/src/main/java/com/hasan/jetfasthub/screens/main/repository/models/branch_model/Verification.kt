package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class Verification(
    val payload: Any,
    val reason: String,
    val signature: Any,
    val verified: Boolean
)