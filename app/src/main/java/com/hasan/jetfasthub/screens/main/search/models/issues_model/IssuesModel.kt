package com.hasan.jetfasthub.screens.main.search.models.issues_model

data class IssuesModel(
    val incomplete_results: Boolean,
    val items: List<IssuesItem>,
    val total_count: Int?
)