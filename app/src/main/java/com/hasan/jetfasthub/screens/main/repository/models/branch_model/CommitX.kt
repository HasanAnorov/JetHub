package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class CommitX(
    val author: AuthorX,
    val comment_count: Int,
    val committer: Committer,
    val message: String,
    val tree: Tree,
    val url: String,
    val verification: Verification
)