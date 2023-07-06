package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface SearchRepository {

    suspend fun searchRepositories(
        token: String,
        query: String,
        page: Long
    ): Response<RepositoryModel>

    suspend fun searchUsers(token: String, query: String, page: Long): Response<UserModel>
    suspend fun searchIssues(token: String, query: String, page: Long): Response<IssuesModel>
    suspend fun searchCodes(token: String, query: String, page: Long): Response<CodeModel>

}


class SearchRepositoryImpl(private val context: Context) : SearchRepository {

    override suspend fun searchRepositories(
        token: String,
        query: String,
        page: Long
    ): Response<RepositoryModel> {
        return RetrofitInstance(context).gitHubService.searchRepositories(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            query = query,
            page = page
        )
    }

    override suspend fun searchUsers(
        token: String,
        query: String,
        page: Long
    ): Response<UserModel> {
        return RetrofitInstance(context).gitHubService.searchUsers(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            query = query,
            page = page
        )
    }

    override suspend fun searchIssues(
        token: String,
        query: String,
        page: Long
    ): Response<IssuesModel> {
        return RetrofitInstance(context).gitHubService.searchIssues(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            query = query,
            page = page,
        )
    }

    override suspend fun searchCodes(
        token: String,
        query: String,
        page: Long
    ): Response<CodeModel> {
        return RetrofitInstance(context = context).gitHubService.searchCodes(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            query = query,
            page = page
        )
    }

}