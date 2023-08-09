package com.hasan.jetfasthub.screens.main.gists.gist_model

data class GistFileModel(
    val fileName: String,
    val textType: String,
    val fileSize : Int,
    val raw_url: String
)