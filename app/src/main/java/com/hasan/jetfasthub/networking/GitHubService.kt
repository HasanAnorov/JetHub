package com.hasan.jetfasthub.networking

import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.login.model.AuthModel
import com.hasan.jetfasthub.screens.main.gists.model.StarredGistModel
import com.hasan.jetfasthub.screens.main.gists.public_gist_model.PublicGistsModel
import com.hasan.jetfasthub.screens.main.home.received_model.ReceivedEvents
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface GitHubService {

    @POST("authorizations")
    fun login(
        @Body authModel: AuthModel
    ): Call<AccessTokenModel>

    @FormUrlEncoded
    @POST("${Constants.BASIC_AUTH_URL}login/oauth/access_token")
    @Headers(
        "Accept: application/json",
    )
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        //other parameters are optional
    ): Response<AccessTokenModel>


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
    ): Response<GistModel>

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
    ): Response<PublicGistsModel    >

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/received_events")
    suspend fun getReceivedUserEvents(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<ReceivedEvents>

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
    ):Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}/members")
    suspend fun getOrganisationMembers(
        @Header("Authorization") authToken: String,
        @Path("org") organisation: String,
        @Query("page") page: Int
    ):Response<OrganisationMemberModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}/repos")
    suspend fun getOrganisationsRepositories(
        @Header("Authorization") authToken: String,
        @Path("org") org: String,
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<OrganisationsRepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("notifications")
    suspend fun getAllNotifications(
        @Query("all") all: Boolean,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authToken: String,
    ): Response<Notification>

    @Headers("Accept: application/vnd.github+json")
    @GET("notifications")
    suspend fun getUnreadNotifications(
        @Header("Authorization") authToken: String,
        @Query("since") since: String
    ): Response<Notification>

    @Headers("Accept: application/vnd.github+json")
    @PATCH("notifications/threads/{thread_id}")
    suspend fun markAsRead(
        @Header("Authorization") authToken: String,
        @Path("thread_id") threadId: String
    ): Response<Int>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ):Response<RepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/users")
    suspend fun searchUsers(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ):Response<UserModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/issues")
    suspend fun searchIssues(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ):Response<IssuesModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/code")
    suspend fun searchCodes(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ):Response<CodeModel>


}