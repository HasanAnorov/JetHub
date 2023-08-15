package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.file_view.file_model.FileModel
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FilesModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response


interface FileViewRepository{

    suspend fun getContentFile(
        token: String,
        owner: String,
        repo: String,
        path: String,
        ref: String
    ): Response<FileModel>

}

class FileViewRepositoryImpl(private val context: Context): FileViewRepository {

    override suspend fun getContentFile(
        token: String,
        owner: String,
        repo: String,
        path: String,
        ref: String
    ): Response<FileModel> {
        return RestClient(context).fileViewRepository.getContentFile(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
            path = path,
            ref = ref
        )
    }

}