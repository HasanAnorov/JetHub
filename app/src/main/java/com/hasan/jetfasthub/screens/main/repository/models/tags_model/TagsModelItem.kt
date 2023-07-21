package com.hasan.jetfasthub.screens.main.repository.models.tags_model

data class TagsModelItem(
    val commit: Commit,
    val name: String,
    val node_id: String,
    val tarball_url: String,
    val zipball_url: String
)