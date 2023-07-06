package com.hasan.jetfasthub.screens.main.search.models.code_model

data class CodeModel(
    val incomplete_results: Boolean,
    val items: List<CodeItem>,
    val total_count: Int
)