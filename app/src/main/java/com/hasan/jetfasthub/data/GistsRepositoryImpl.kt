package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.gists.model.StarredGistModel
import com.hasan.jetfasthub.screens.main.gists.public_gist_model.PublicGistsModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface GistsRepository {

    suspend fun getUserGists(token: String, username: String, page: Int): Response<GistsModel>

    suspend fun getStarredGists(token: String, page: Int): Response<StarredGistModel>

    suspend fun getPublicGists(token: String, perPage: Int, page: Int): Response<PublicGistsModel>

}

class GistsRepositoryImpl(private val context: Context) : GistsRepository {

    override suspend fun getUserGists(
        token: String,
        username: String,
        page: Int
    ): Response<GistsModel> {
        return RestClient(context).gistService.getUserGists(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            username = username,
            page = page
        )
    }

    override suspend fun getStarredGists(token: String, page: Int): Response<StarredGistModel> {
        return RestClient(context).gistService.getStarredGists(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            page = page
        )
    }

    override suspend fun getPublicGists(
        token: String,
        perPage: Int,
        page: Int
    ): Response<PublicGistsModel> {
        return RestClient(context).gistService.getPublicGists(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            perPage = perPage,
            page = page
        )
    }

}