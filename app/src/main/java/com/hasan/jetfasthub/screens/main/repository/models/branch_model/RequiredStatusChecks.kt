package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class RequiredStatusChecks(
    val contexts: List<String>,
    val enforcement_level: String
)