package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.repository.models.branch_model.BranchModel
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
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import retrofit2.Response

interface Repository {

    suspend fun getRepo(token: String, owner: String, repo: String): Response<RepoModel>

    suspend fun getContributors(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<Contributors>

    suspend fun getReleases(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<ReleasesModel>

    suspend fun getReadmeAsHtml(token: String, url: String): Response<String>

    suspend fun getContentFiles(
        token: String,
        owner: String,
        repo: String,
        path: String,
        ref: String
    ): Response<FilesModel>

    suspend fun getBranches(
        token: String,
        owner: String,
        repo: String
    ): Response<BranchModel>

    suspend fun getCommits(
        token: String,
        owner: String,
        repo: String,
        branch: String,
        path: String,
        page: Int
    ): Response<CommitsModel>

    suspend fun isWatchingRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<RepoSubscriptionModel>

    suspend fun watchRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<RepoSubscriptionModel>

    suspend fun unwatchRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<Boolean>

    suspend fun getWatchers(
        token: String,
        owner: String,
        repo: String
    ): Response<SubscriptionsModel>

    suspend fun getStargazers(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<StargazersModel>

    suspend fun checkStarring(
        token: String,
        owner: String,
        repo: String,
    ): Response<Boolean>

    suspend fun starRepo(
        token: String,
        owner: String,
        repo: String,
    ): Response<Boolean>

    suspend fun unStarRepo(
        token: String,
        owner: String,
        repo: String,
    ): Response<Boolean>

    suspend fun getForks(
        token: String,
        owner: String,
        repo: String,
    ): Response<ForksModel>

    suspend fun forkRepo(
        token: String,
        owner: String,
        repo: String,
    ): Response<ForkResponseModel>

    suspend fun getLicense(
        token: String, owner: String, repo: String
    ): Response<LicenseModel>

    suspend fun getLabels(
        token: String, owner: String, repo: String, page: Int
    ): Response<LabelsModel>

    suspend fun getTags(token: String, owner: String, repo: String, page: Int): Response<TagsModel>

}

class RepositoryImpl(private val context: Context) : Repository {

    override suspend fun getLabels(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<LabelsModel> {
        return RetrofitInstance(context).gitHubService.getLabels(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun getTags(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<TagsModel> {
        return RetrofitInstance(context).gitHubService.getTags(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun getRepo(token: String, owner: String, repo: String): Response<RepoModel> {
        return RetrofitInstance(context).gitHubService.getRepo(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo
        )
    }

    override suspend fun getContributors(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<Contributors> {
        return RetrofitInstance(context).gitHubService.getContributors(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun getReleases(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<ReleasesModel> {
        return RetrofitInstance(context).gitHubService.getReleases(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun getReadmeAsHtml(token: String, url: String): Response<String> {
        return RetrofitInstance(context).gitHubService.getReadmeAsHtml(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            url = url
        )

    }

    override suspend fun getContentFiles(
        token: String,
        owner: String,
        repo: String,
        path: String,
        ref: String
    ): Response<FilesModel> {
        return RetrofitInstance(context).gitHubService.getContentFiles(
            token = token,
            owner = owner,
            repo = repo,
            path = path,
            ref = ref
        )
    }

    override suspend fun getBranches(
        token: String,
        owner: String,
        repo: String
    ): Response<BranchModel> {
        return RetrofitInstance(context).gitHubService.getBranches(
            token = token,
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getCommits(
        token: String,
        owner: String,
        repo: String,
        branch: String,
        path: String,
        page: Int
    ): Response<CommitsModel> {
        return RetrofitInstance(context).gitHubService.getCommits(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            branch = branch,
            path = path,
            page = page
        )
    }

    override suspend fun isWatchingRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<RepoSubscriptionModel> {
        return RetrofitInstance(context).gitHubService.isWatchingRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo
        )
    }

    override suspend fun watchRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<RepoSubscriptionModel> {
        return RetrofitInstance(context).gitHubService.watchRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo
        )
    }

    override suspend fun unwatchRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<Boolean> {
        return RetrofitInstance(context).gitHubService.unwatchRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo
        )
    }

    override suspend fun getWatchers(
        token: String,
        owner: String,
        repo: String
    ): Response<SubscriptionsModel> {
        return RetrofitInstance(context).gitHubService.getWatchers(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo
        )
    }

    override suspend fun getStargazers(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<StargazersModel> {
        return RetrofitInstance(context).gitHubService.getStargazers(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun checkStarring(
        token: String,
        owner: String,
        repo: String
    ): Response<Boolean> {
        return RetrofitInstance(context).gitHubService.checkStarring(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun starRepo(token: String, owner: String, repo: String): Response<Boolean> {
        return RetrofitInstance(context).gitHubService.starRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun unStarRepo(token: String, owner: String, repo: String): Response<Boolean> {
        return RetrofitInstance(context).gitHubService.unStarRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getForks(
        token: String,
        owner: String,
        repo: String
    ): Response<ForksModel> {
        return RetrofitInstance(context).gitHubService.getForks(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun forkRepo(
        token: String,
        owner: String,
        repo: String
    ): Response<ForkResponseModel> {
        return RetrofitInstance(context).gitHubService.forkRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getLicense(
        token: String,
        owner: String,
        repo: String
    ): Response<LicenseModel> {
        return RetrofitInstance(context).gitHubService.getLicense(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

}