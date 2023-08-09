package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class RequiredStatusChecks(
    val checks: List<Any>,
    val contexts: List<Any>,
    val enforcement_level: String
)