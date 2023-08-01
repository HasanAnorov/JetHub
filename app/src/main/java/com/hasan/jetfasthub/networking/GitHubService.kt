package com.hasan.jetfasthub.networking

import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.login.model.AuthModel
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentPostResponse
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model.CommitCommentsModel
import com.hasan.jetfasthub.screens.main.gists.model.StarredGistModel
import com.hasan.jetfasthub.screens.main.gists.public_gist_model.PublicGistsModel
import com.hasan.jetfasthub.screens.main.home.authenticated_user.AuthenticatedUser
import com.hasan.jetfasthub.screens.main.home.received_events_model.ReceivedEventsModel
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.organisations.organisation_model.OrganisationModel
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
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
import com.hasan.jetfasthub.screens.main.commits.models.commit_model.CommitModel
import com.hasan.jetfasthub.screens.main.gists.fork_response_model.GistForkResponse
import com.hasan.jetfasthub.screens.main.gists.gist_comment_response.GistCommentResponse
import com.hasan.jetfasthub.screens.main.gists.gist_comments_model.GistCommentsModel
import com.hasan.jetfasthub.screens.main.gists.gist_model.GistModel
import com.hasan.jetfasthub.screens.main.repository.models.branch_model.BranchModel
import com.hasan.jetfasthub.screens.main.repository.models.stargazers_model.StargazersModel
import com.hasan.jetfasthub.screens.main.repository.models.subscriptions_model.SubscriptionsModel
import com.hasan.jetfasthub.screens.main.repository.models.tags_model.TagsModel
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
import retrofit2.http.Url

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
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String,
    ): Response<AuthenticatedUser>


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
    @PATCH("gists/{gist_id}/comments/{comment_id}")
    suspend fun editGistComment(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String,
        @Path("comment_id") commentId: Int,
        @Body body: CommentRequestModel
    ): Response<GistCommentResponse>

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

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/received_events")
    suspend fun getReceivedUserEvents(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<ReceivedEventsModel>

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
    @GET("orgs/{org}/members")
    suspend fun getOrganisationMembers(
        @Header("Authorization") authToken: String,
        @Path("org") organisation: String,
        @Query("page") page: Int
    ): Response<OrganisationMemberModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}/repos")
    suspend fun getOrganisationsRepositories(
        @Header("Authorization") authToken: String,
        @Path("org") org: String,
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<OrganisationsRepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}")
    suspend fun getOrganisation(
        @Header("Authorization") authToken: String,
        @Path("org") org: String,
    ): Response<OrganisationModel>

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
    ): Response<RepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/users")
    suspend fun searchUsers(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<UserModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/issues")
    suspend fun searchIssues(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<IssuesModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/code")
    suspend fun searchCodes(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<CodeModel>

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

    @Headers("Accept: application/vnd.github+json, application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    @GET("repos/{owner}/{repo}/commits/{ref}")
    suspend fun getCommit(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") branch: String,
    ): Response<CommitModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/commits/{commit_sha}/comments")
    suspend fun getCommitComments(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("commit_sha") ref: String,
    ): Response<CommitCommentsModel>

    @Headers("Accept: application/vnd.github+json")
    @PATCH("repos/{owner}/{repo}/comments/{comment_id}")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Int,
        @Body body: CommentRequestModel
    ): Response<CommentPostResponse>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("repos/{owner}/{repo}/comments/{comment_id}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Int,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json, application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    @POST("repos/{owner}/{repo}/commits/{commit_sha}/comments")
    suspend fun postCommitComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("commit_sha") ref: String,
        @Body body: CommentRequestModel
    ): Response<CommentPostResponse>

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