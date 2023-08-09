package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.repository.models.branch_model.BranchModel
import com.hasan.jetfasthub.screens.main.repository.models.branches_model.BranchesModel
import com.hasan.jetfasthub.screens.main.repository.models.commits_model.CommitsModel
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FilesModel
import com.hasan.jetfasthub.screens.main.repository.models.fork_response_model.ForkResponseModel
import com.hasan.jetfasthub.screens.main.repository.models.forks_model.ForksModel
import com.hasan.jetfasthub.screens.main.repository.models.labels_model.LabelsModel
import com.hasan.jetfasthub.screens.main.repository.models.license_model.LicenseModel
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModel
import com.hasan.jetfasthub.screens.main.repository.models.repo_contributor_model.Contributors
import com.hasan.jetfasthub.screens.main.repository.models.repo_model.RepoModel
import com.hasan.jetfasthub.screens.main.repository.models.repo_subscription_model.RepoSubscriptionModel
import com.hasan.jetfasthub.screens.main.repository.models.stargazers_model.StargazersModel
import com.hasan.jetfasthub.screens.main.repository.models.subscriptions_model.SubscriptionsModel
import com.hasan.jetfasthub.screens.main.repository.models.tags_model.TagsModel
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RepositoryService {

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<RepoModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Response<Contributors>

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/releases")
    suspend fun getReleases(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Response<ReleasesModel>

    @Headers("Accept: application/vnd.github.html")
    suspend fun getReadmeAsHtml(
        @Header("Authorization") token: String,
        @Url url: String
    ): Response<String>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContentFiles(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Query("ref") ref: String
    ): Response<FilesModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<BranchesModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/branches/{branch}")
    suspend fun getBranch(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String
    ): Response<BranchModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/labels")
    suspend fun getLabels(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Response<LabelsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/tags")
    suspend fun getTags(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Response<TagsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") branch: String,
        @Query("path") path: String,
        @Query("page") page: Int
    ): Response<CommitsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/subscription")
    suspend fun isWatchingRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<RepoSubscriptionModel>

    @Headers("Accept: application/vnd.github+json")
    @PUT("repos/{owner}/{repo}/subscription")
    suspend fun watchRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<RepoSubscriptionModel>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("repos/{owner}/{repo}/subscription")
    suspend fun unwatchRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/subscribers")
    suspend fun getWatchers(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<SubscriptionsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/stargazers")
    suspend fun getStargazers(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Response<StargazersModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("user/starred/{owner}/{repo}")
    suspend fun checkStarring(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @PUT("user/starred/{owner}/{repo}")
    suspend fun starRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("user/starred/{owner}/{repo}")
    suspend fun unStarRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/forks")
    suspend fun getForks(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<ForksModel>

    @Headers("Accept: application/vnd.github+json")
    @POST("repos/{owner}/{repo}/forks")
    suspend fun forkRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<ForkResponseModel>

    @Headers("Accept: application/vnd.github.html")
    @GET("repos/{owner}/{repo}/license")
    suspend fun getLicense(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<LicenseModel>

}