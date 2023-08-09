package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import retrofit2.Response

interface ProfileRepository {

    suspend fun getUser(token: String, username: String): Response<GitHubUser>

    suspend fun getUserOrganisations(token: String, username: String): Response<OrgModel>

    suspend fun getUserEvents(token: String, username: String): Response<UserEvents>

    suspend fun getUserRepository(token: String, username: String): Response<UserRepositoryModel>

    suspend fun getUserStarredRepos(
        token: String,
        username: String,
        page: Int
    ): Response<StarredRepoModel>

    suspend fun getUserStarredReposCount(
        token: String,
        username: String,
        per_page: Int
    ): Response<StarredRepoModel>

    suspend fun getUserFollowings(
        token: String,
        username: String,
        page: Int
    ): Response<FollowingModel>

    suspend fun getUserFollowers(
        token: String,
        username: String,
        page: Int
    ): Response<FollowersModel>

    suspend fun getUserGists(token: String, username: String, page: Int): Response<GistsModel>

    suspend fun followUser(token: String, username: String): Response<Boolean>

    suspend fun unfollowUser(token: String, username: String): Response<Boolean>

    suspend fun getFollowStatus(token: String, username: String): Response<Boolean>

    suspend fun isUserBlocked(token: String, username: String): Response<Boolean>

    suspend fun blockUser(token: String, username: String): Response<Boolean>

    suspend fun unblockUser(token: String, username: String): Response<Boolean>

}

class ProfileRepositoryImpl(private val context: Context) : ProfileRepository {

    override suspend fun getUser(token: String, username: String): Response<GitHubUser> {
        return RestClient(context = context).profileService.getUser(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun getUserOrganisations(token: String, username: String): Response<OrgModel> {
        return RestClient(context = context).profileService.getUserOrgs(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun getUserEvents(token: String, username: String): Response<UserEvents> {
        return RestClient(context).profileService.getUserEvents(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun getUserRepository(
        token: String,
        username: String
    ): Response<UserRepositoryModel> {
        return RestClient(context).profileService.getUserRepos(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun getUserStarredRepos(
        token: String,
        username: String,
        page: Int
    ): Response<StarredRepoModel> {
        return RestClient(context).profileService.getUserStarredRepos(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
            page = page
        )
    }

    override suspend fun getUserStarredReposCount(
        token: String,
        username: String,
        per_page: Int
    ): Response<StarredRepoModel> {
        return RestClient(context).profileService.getUserStarredReposCount(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
            per_page = per_page
        )
    }

    override suspend fun getUserFollowings(
        token: String,
        username: String,
        page: Int
    ): Response<FollowingModel> {
        return RestClient(context).profileService.getUserFollowings(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
            page = page
        )
    }

    override suspend fun getUserFollowers(
        token: String,
        username: String,
        page: Int
    ): Response<FollowersModel> {
        return RestClient(context).profileService.getUserFollowers(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
            page = page
        )
    }

    override suspend fun getUserGists(
        token: String,
        username: String,
        page: Int
    ): Response<GistsModel> {
        return RestClient(context).profileService.getUserGists(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
            page = page
        )
    }

    override suspend fun followUser(token: String, username: String): Response<Boolean> {
        return RestClient(context).profileService.followUser(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username,
        )
    }

    override suspend fun getFollowStatus(token: String, username: String): Response<Boolean> {
        return RestClient(context = context).profileService.getFollowStatus(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun unfollowUser(token: String, username: String): Response<Boolean> {
        return RestClient(context).profileService.unfollowUser(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun isUserBlocked(token: String, username: String): Response<Boolean> {
        return RestClient(context).profileService.isUserBlocked(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun blockUser(token: String, username: String): Response<Boolean> {
        return RestClient(context).profileService.blockUser(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

    override suspend fun unblockUser(token: String, username: String): Response<Boolean> {
        return RestClient(context).profileService.unblockUser(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            username = username
        )
    }

}