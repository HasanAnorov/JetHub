package com.hasan.jetfasthub.screens.main.repository.models.branches_model

data class RequiredStatusChecks(
    val contexts: List<String>,
    val enforcement_level: String
)