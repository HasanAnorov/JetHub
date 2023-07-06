package com.hasan.jetfasthub.screens.main.search.models.repository_model

data class RepositoryModel(
    val incomplete_results: Boolean,
    val items: List<Item>,
    val total_count: Int?
)