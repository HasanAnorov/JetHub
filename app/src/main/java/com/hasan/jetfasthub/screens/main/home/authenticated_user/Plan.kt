package com.hasan.jetfasthub.screens.main.home.authenticated_user

data class Plan(
    val collaborators: Int,
    val name: String,
    val private_repos: Int,
    val space: Int
)