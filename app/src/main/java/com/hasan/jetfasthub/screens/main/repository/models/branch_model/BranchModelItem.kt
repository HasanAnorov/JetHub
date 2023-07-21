package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class BranchModelItem(
    val commit: Commit,
    val name: String,
    val `protected`: Boolean,
    val protection: Protection,
    val protection_url: String
)