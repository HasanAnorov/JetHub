package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.home.data.remote.user_model.GitHubUser
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}")
    suspend fun getUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<GitHubUser>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/orgs")
    suspend fun getUserOrgs(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<OrgModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<UserEvents>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<UserRepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/starred")
    suspend fun getUserStarredRepos(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("page") page: Int,
    ): Response<StarredRepoModel>


    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/starred")
    suspend fun getUserStarredReposCount(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("per_page") per_page: Int
    ): Response<StarredRepoModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/following")
    suspend fun getUserFollowings(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("page") page: Int,
    ): Response<FollowingModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("page") page: Int,
    ): Response<FollowersModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/gists")
    suspend fun getUserGists(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Query("page") page: Int,
    ): Response<GistsModel>

    @Headers("Accept: application/vnd.github+json")
    @PUT("user/following/{username}")
    suspend fun followUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("user/following/{username}")
    suspend fun unfollowUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("user/following/{username}")
    suspend fun getFollowStatus(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("user/blocks/{username}")
    suspend fun isUserBlocked(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @PUT("user/blocks/{username}")
    suspend fun blockUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("user/blocks/{username}")
    suspend fun unblockUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<Boolean>

}