package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class Protection(
    val enabled: Boolean,
    val required_status_checks: RequiredStatusChecks
)