package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.gists.fork_response_model.GistForkResponse
import com.hasan.jetfasthub.screens.main.gists.gist_comment_response.GistCommentResponse
import com.hasan.jetfasthub.screens.main.gists.gist_comments_model.GistCommentsModel
import com.hasan.jetfasthub.screens.main.gists.gist_model.GistModel
import com.hasan.jetfasthub.screens.main.gists.model.StarredGistModel
import com.hasan.jetfasthub.screens.main.gists.public_gist_model.PublicGistsModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GistService {

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/gists")
    suspend fun getUserGists(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("page") page: Int,
    ): Response<GistsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("gists/{gist_id}")
    suspend fun getGist(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<GistModel>

    @Headers("Accept: application/vnd.github+json")
    @POST("gists/{gist_id}/comments")
    suspend fun postGistComment(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String,
        @Body body: CommentRequestModel
    ): Response<GistCommentResponse>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("gists/{gist_id}/comments/{comment_id}")
    suspend fun deleteGistComment(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String,
        @Path("comment_id") commentId: Int,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("gists/{gist_id}/comments")
    suspend fun getGistComments(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<GistCommentsModel>

    @Headers("Accept: application/vnd.github+json")
    @POST("gists/{gist_id}/forks")
    suspend fun forkGist(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<GistForkResponse>

    @Headers("Accept: application/vnd.github+json")
    @GET("gists/{gist_id}/star")
    suspend fun checkIfGistStarred(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("gists/{gist_id}")
    suspend fun deleteGist(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @PUT("gists/{gist_id}/star")
    suspend fun starGist(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("gists/{gist_id}/star")
    suspend fun unstarGist(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("gists/starred")
    suspend fun getStarredGists(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
    ): Response<StarredGistModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("gists/public")
    suspend fun getPublicGists(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
    ): Response<PublicGistsModel>

}