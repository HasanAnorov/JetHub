package com.hasan.jetfasthub.screens.main.repository.models.forks_model

data class Permissions(
    val admin: Boolean,
    val pull: Boolean,
    val push: Boolean
)