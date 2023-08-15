package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.repository.models.branch_model.BranchModel
import com.hasan.jetfasthub.screens.main.repository.models.branches_model.BranchesModel
import com.hasan.jetfasthub.screens.main.repository.models.commits_model.CommitsModel
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FilesModel
import com.hasan.jetfasthub.screens.main.repository.models.fork_response_model.ForkResponseModel
import com.hasan.jetfasthub.screens.main.repository.models.forks_model.ForksModel
import com.hasan.jetfasthub.screens.main.repository.models.issues_model.RepoIssuesModel
import com.hasan.jetfasthub.screens.main.repository.models.labels_model.LabelsModel
import com.hasan.jetfasthub.screens.main.repository.models.license_response_model.LicenseResponse
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModel
import com.hasan.jetfasthub.screens.main.repository.models.repo_contributor_model.Contributors
import com.hasan.jetfasthub.screens.main.repository.models.repo_model.RepoModel
import com.hasan.jetfasthub.screens.main.repository.models.repo_subscription_model.RepoSubscriptionModel
import com.hasan.jetfasthub.screens.main.repository.models.stargazers_model.StargazersModel
import com.hasan.jetfasthub.screens.main.repository.models.subscriptions_model.SubscriptionsModel
import com.hasan.jetfasthub.screens.main.repository.models.tags_model.TagsModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import retrofit2.Response

interface Repository {

    suspend fun getRepo(token: String, owner: String, repo: String): Response<RepoModel>

    suspend fun getIssuesWithCount(token: String, query: String, page: Int): Response<IssuesModel>

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
    suspend fun getReadMeMarkDown(
        token: String,
        owner: String,
        repo: String,
        branch:String
    ): Response<String>

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
    ): Response<BranchesModel>

    suspend fun getBranch(
        token: String,
        owner: String,
        repo: String,
        branch: String
    ): Response<BranchModel>

    suspend fun getRepoIssues(
        token: String,
        owner: String,
        repo: String,
        state: String,
        sortBy: String,
        page: Int
    ): Response<RepoIssuesModel>

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
        token: String, license: String
    ): Response<LicenseResponse>

    suspend fun getLabels(
        token: String, owner: String, repo: String, page: Int
    ): Response<LabelsModel>

    suspend fun getTags(token: String, owner: String, repo: String, page: Int): Response<TagsModel>

}

class RepositoryImpl(private val context: Context) : Repository {


    override suspend fun getIssuesWithCount(
        token: String,
        query: String,
        page: Int
    ): Response<IssuesModel> {
        return RestClient(context).repositoryService.getIssuesWithCount(
            authToken = "Bearer $PERSONAL_ACCESS_TOKEN",
            query = query,
            page = page,
        )
    }

    override suspend fun getReadMeMarkDown(
        token: String,
        owner: String,
        repo: String,
        branch: String
    ): Response<String> {
        return RestClient(context).repositoryService.getReadMeMarkdown(token, owner, repo, branch)
    }

    override suspend fun getLabels(
        token: String,
        owner: String,
        repo: String,
        page: Int
    ): Response<LabelsModel> {
        return RestClient(context).repositoryService.getLabels(
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
        return RestClient(context).repositoryService.getTags(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }

    override suspend fun getRepo(token: String, owner: String, repo: String): Response<RepoModel> {
        return RestClient(context).repositoryService.getRepo(
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
        return RestClient(context).repositoryService.getContributors(
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
        return RestClient(context).repositoryService.getReleases(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            page = page
        )
    }


    override suspend fun getContentFiles(
        token: String,
        owner: String,
        repo: String,
        path: String,
        ref: String
    ): Response<FilesModel> {
        return RestClient(context).repositoryService.getContentFiles(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
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
    ): Response<BranchesModel> {
        return RestClient(context).repositoryService.getBranches(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getBranch(
        token: String,
        owner: String,
        repo: String,
        branch: String
    ): Response<BranchModel> {
        return RestClient(context).repositoryService.getBranch(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            branch = branch,
        )
    }

    override suspend fun getRepoIssues(
        token: String,
        owner: String,
        repo: String,
        state: String,
        sortBy: String,
        page: Int
    ): Response<RepoIssuesModel> {
        return RestClient(context).repositoryService.getIssues(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
            state = state,
            sortBy = sortBy,
            page = page
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
        return RestClient(context).repositoryService.getCommits(
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
        return RestClient(context).repositoryService.isWatchingRepo(
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
        return RestClient(context).repositoryService.watchRepo(
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
        return RestClient(context).repositoryService.unwatchRepo(
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
        return RestClient(context).repositoryService.getWatchers(
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
        return RestClient(context).repositoryService.getStargazers(
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
        return RestClient(context).repositoryService.checkStarring(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun starRepo(token: String, owner: String, repo: String): Response<Boolean> {
        return RestClient(context).repositoryService.starRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun unStarRepo(token: String, owner: String, repo: String): Response<Boolean> {
        return RestClient(context).repositoryService.unStarRepo(
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
        return RestClient(context).repositoryService.getForks(
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
        return RestClient(context).repositoryService.forkRepo(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getLicense(
        token: String,
        license: String
    ): Response<LicenseResponse> {
        return RestClient(context).repositoryService.getLicense(
            token = "Bearer $PERSONAL_ACCESS_TOKEN",
            license = license
        )
    }

}