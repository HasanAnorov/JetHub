package com.hasan.jetfasthub.screens.main.home.user_model

data class Plan(
    val collaborators: Int,
    val name: String,
    val private_repos: Int,
    val space: Int
)