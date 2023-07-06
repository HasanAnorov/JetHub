package com.hasan.jetfasthub.screens.main.search.models.users_model

data class UserModel(
    val incomplete_results: Boolean,
    val items: List<UsersItem>,
    val total_count: Int?
)