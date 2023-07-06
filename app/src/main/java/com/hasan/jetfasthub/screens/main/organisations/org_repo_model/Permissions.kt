package com.hasan.jetfasthub.screens.main.organisations.org_repo_model

data class Permissions(
    val admin: Boolean,
    val pull: Boolean,
    val push: Boolean
)