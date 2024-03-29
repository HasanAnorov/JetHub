package com.hasan.jetfasthub.screens.main.home.data.remote.issues_model

data class Milestone(
    val closed_at: String,
    val closed_issues: Int,
    val created_at: String,
    val creator: Creator,
    val description: String,
    val due_on: String,
    val html_url: String,
    val id: Int,
    val labels_url: String,
    val node_id: String,
    val number: Int,
    val open_issues: Int,
    val state: String,
    val title: String,
    val updated_at: String,
    val url: String
)