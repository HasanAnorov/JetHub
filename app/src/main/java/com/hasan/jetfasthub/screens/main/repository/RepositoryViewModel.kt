package com.hasan.jetfasthub.screens.main.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.Repository
import com.hasan.jetfasthub.data.download.Downloader
import com.hasan.jetfasthub.screens.main.repository.models.branch_model.BranchModel
import com.hasan.jetfasthub.screens.main.repository.models.branches_model.BranchesModel
import com.hasan.jetfasthub.screens.main.repository.models.commits_model.CommitsModel
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FilesModel
import com.hasan.jetfasthub.screens.main.repository.models.forks_model.ForksModel
import com.hasan.jetfasthub.screens.main.repository.models.issues_model.RepoIssuesModel
import com.hasan.jetfasthub.screens.main.repository.models.labels_model.LabelsModel
import com.hasan.jetfasthub.screens.main.repository.models.license_response_model.LicenseResponse
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModel
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModelItem
import com.hasan.jetfasthub.screens.main.repository.models.repo_contributor_model.Contributors
import com.hasan.jetfasthub.screens.main.repository.models.repo_model.RepoModel
import com.hasan.jetfasthub.screens.main.repository.models.repo_subscription_model.RepoSubscriptionModel
import com.hasan.jetfasthub.screens.main.repository.models.release_download_model.ReleaseDownloadModel
import com.hasan.jetfasthub.screens.main.repository.models.stargazers_model.StargazersModel
import com.hasan.jetfasthub.screens.main.repository.models.subscriptions_model.SubscriptionsModel
import com.hasan.jetfasthub.screens.main.repository.models.tags_model.TagsModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.utility.IssueState
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RepositoryViewModel(
    private val repository: Repository,
    private val downloader: Downloader
) : ViewModel() {

    private var _state: MutableStateFlow<RepositoryScreenState> = MutableStateFlow(
        RepositoryScreenState()
    )
    val state = _state.asStateFlow()

    fun downloadRelease(releaseDownloadModel: ReleaseDownloadModel) {
        viewModelScope.launch {
            downloader.downloadRelease(releaseDownloadModel)
        }
    }

    fun downloadRepo(url: String, message: String) {
        viewModelScope.launch {
            downloader.downloadRepo(url, message)
        }
    }

    fun downloadFile(url: String, message: String) {
        viewModelScope.launch {
            downloader.downloadFile(url, message)
        }
    }

    fun onBottomBarItemClicked(repositoryScreen: RepositoryScreens) {
        _state.update {
            it.copy(selectedBottomBarItem = repositoryScreen)
        }
    }

    fun onBottomSheetChanged(bottomSheet: BottomSheetScreens) {
        _state.update {
            it.copy(currentSheet = bottomSheet)
        }
    }

    fun getRepo(token: String, owner: String, repo: String) {
        viewModelScope.launch {
            try {
                repository.getRepo(token, owner, repo).let { repo ->
                    if (repo.isSuccessful) {
                        repo.body()!!.license?.let { getLicense(token, it.key) }
                        _state.update {
                            it.copy(Repository = Resource.Success(repo.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(Repository = Resource.Failure(repo.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Repository = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getContributors(token: String, owner: String, repo: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getContributors(token, owner, repo, page).let { contributorsResponse ->
                    if (contributorsResponse.isSuccessful) {
                        _state.update {
                            it.copy(Contributors = Resource.Success(contributorsResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Contributors = Resource.Failure(
                                    contributorsResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Contributors = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getReleases(token: String, owner: String, repo: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getReleases(token, owner, repo, page).let { releasesModelResponse ->
                    if (releasesModelResponse.isSuccessful) {
                        _state.update {
                            it.copy(Releases = Resource.Success(releasesModelResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Releases = Resource.Failure(
                                    releasesModelResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Releases = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getContentFiles(token: String, owner: String, repo: String, path: String, ref: String) {
        viewModelScope.launch {
            try {
                repository.getContentFiles(token, owner, repo, path, ref).let { contentFiles ->
                    if (contentFiles.isSuccessful) {
                        _state.update {
                            it.copy(
                                RepositoryFiles = Resource.Success(contentFiles.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                RepositoryFiles = Resource.Failure(
                                    contentFiles.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        RepositoryFiles = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getBranches(token: String, owner: String, repo: String): Flow<BranchesModel> =
        callbackFlow {
            viewModelScope.launch {
                try {
                    repository.getBranches(token, owner, repo).let { branches ->
                        if (branches.isSuccessful) {
                            trySend(branches.body()!!)
                            _state.update {
                                it.copy(Branches = Resource.Success(branches.body()!!))
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    Branches = Resource.Failure(
                                        branches.errorBody().toString()
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(Branches = Resource.Failure(e.message.toString()))
                    }
                }
            }
            awaitClose {
                channel.close()
                Log.d("callback_ahi", "callback stop : ")
            }
        }

    fun getBranch(token: String, repo: String, owner: String, branch: String) {
        _state.update {
            it.copy(
                RepoDownloadLink = Resource.Loading()
            )
        }
        viewModelScope.launch {
            try {
                repository.getBranch(token, owner, repo, branch).let { branch ->
                    if (branch.isSuccessful) {
                        _state.update {
                            it.copy(Branch = Resource.Success(branch.body()!!))
                        }
                        updateDownloadLink(Resource.Success(branch.body()!!._links.html))
                    } else {
                        _state.update {
                            it.copy(Branch = Resource.Failure(branch.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Branch = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getCommits(
        token: String,
        owner: String,
        repo: String,
        branch: String,
        path: String,
        page: Int
    ) {
        viewModelScope.launch {
            try {
                repository.getCommits(token, owner, repo, branch, path, page).let { commits ->
                    if (commits.isSuccessful) {
                        _state.update {
                            it.copy(Commits = Resource.Success(commits.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(Commits = Resource.Failure(commits.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Commits = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun isWatchingRepo(token: String, owner: String, repo: String) {
        viewModelScope.launch {
            try {
                repository.isWatchingRepo(
                    token, owner, repo
                ).let { isWatchingRepoResponse ->
                    if (isWatchingRepoResponse.isSuccessful) {
                        _state.update {
                            it.copy(
                                isWatching = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isWatching = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "isWatchingRepo: ${e.message} ")
            }
        }
    }

    fun watchRepo(token: String, owner: String, repo: String): Flow<RepoSubscriptionModel> =
        callbackFlow {
            viewModelScope.launch {
                try {
                    repository.watchRepo(
                        token, owner, repo
                    ).let { watchRepoResponse ->
                        if (watchRepoResponse.isSuccessful) {
                            _state.update {
                                it.copy(isWatching = true)
                            }
                            trySend(watchRepoResponse.body()!!)
                        } else {
                            trySend(watchRepoResponse.body()!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ahi3646", "watchRepo: network error ")
                }
            }
            awaitClose {
                channel.close()
                Log.d("ahi3646", "watchRepo: channel closed ")
            }
        }

    fun unwatchRepo(token: String, owner: String, repo: String): Flow<Boolean> =
        callbackFlow {
            viewModelScope.launch {
                try {
                    repository.unwatchRepo(
                        token, owner, repo
                    ).let { watchRepoResponse ->
                        if (watchRepoResponse.code() == 204) {
                            _state.update {
                                it.copy(isWatching = false)
                            }
                            trySend(true)
                        } else {
                            trySend(false)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ahi3646", "watchRepo: network error - ${e.message} ")
                }
            }
            awaitClose {
                channel.close()
                Log.d("ahi3646", "watchRepo: channel closed ")
            }
        }

    /**
    Will implement these later :)

    fun getWatchers(token: String, owner: String, repo: String) {
    viewModelScope.launch {
    try {
    repository.getWatchers(token, owner, repo).let { subscribers ->
    if (subscribers.isSuccessful) {
    _state.update {
    it.copy(
    Watchers = Resource.Success(subscribers.body()!!)
    )
    }
    } else {
    _state.update {
    it.copy(
    Watchers = Resource.Failure(subscribers.errorBody().toString())
    )
    }
    }
    }
    } catch (e: Exception) {
    _state.update {
    it.copy(
    Watchers = Resource.Failure(e.message.toString())
    )
    }
    }
    }
    }

    fun getLicense(token: String, owner: String, repo: String) {
    viewModelScope.launch {
    try {
    repository.getLicense(token, owner, repo).let { licenseResponse ->
    if (licenseResponse.isSuccessful) {
    _state.update {
    it.copy(License = Resource.Success(licenseResponse.body()!!))
    }
    } else {
    _state.update {
    it.copy(
    License = Resource.Failure(
    licenseResponse.errorBody().toString()
    )
    )
    }
    }
    }
    } catch (e: Exception) {
    _state.update {
    it.copy(License = Resource.Failure(e.message.toString()))
    }
    }
    }
    }

    fun getStargazers(token: String, owner: String, repo: String, page: Int) {
    viewModelScope.launch {
    try {
    repository.getStargazers(token, owner, repo, page).let { stargazersModelResponse ->
    if (stargazersModelResponse.isSuccessful) {
    _state.update {
    it.copy(
    Stargazers = Resource.Success(stargazersModelResponse.body()!!)
    )
    }
    } else {
    _state.update {
    it.copy(
    Stargazers = Resource.Failure(
    stargazersModelResponse.errorBody().toString()
    )
    )
    }
    }
    }
    } catch (e: Exception) {
    _state.update {
    it.copy(
    Stargazers = Resource.Failure(e.message.toString())
    )
    }
    }
    }
    }

    fun getForks(token: String, owner: String, repo: String) {
    viewModelScope.launch {
    try {
    repository.getForks(token, owner, repo).let { forksModelResponse ->
    if (forksModelResponse.isSuccessful) {
    _state.update {
    it.copy(
    Forks = Resource.Success(forksModelResponse.body()!!)
    )
    }
    } else {
    _state.update {
    it.copy(
    Forks = Resource.Failure(
    forksModelResponse.errorBody().toString()
    )
    )
    }
    }
    }
    } catch (e: Exception) {
    _state.update {
    it.copy(
    Forks = Resource.Failure(e.message.toString())
    )
    }
    }
    }
    }

     */

    fun isStarringRepo(token: String, owner: String, repo: String) {
        viewModelScope.launch {
            try {
                repository.checkStarring(
                    token, owner, repo
                ).let { starringResponse ->
                    if (starringResponse.code() == 204) {
                        _state.update {
                            it.copy(
                                isStarring = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isStarring = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "isWatchingRepo: ${e.message} ")
            }
        }
    }

    fun starRepo(token: String, owner: String, repo: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.starRepo(token, owner, repo).let { response ->
                    if (response.code() == 204) {
                        _state.update {
                            it.copy(isStarring = true)
                        }
                        trySend(true)
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "starRepo: ${e.message.toString()} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "starRepo: channel close ")
        }
    }

    fun unStarRepo(token: String, owner: String, repo: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.unStarRepo(token, owner, repo).let { response ->
                    if (response.code() == 204) {
                        _state.update {
                            it.copy(isStarring = false)
                        }
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                trySend(false)
                Log.d("ahi3646", "starRepo: ${e.message.toString()} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "unStarRepo: channel closed ")
        }
    }

    fun forkRepo(token: String, owner: String, repo: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.forkRepo(token, owner, repo).let { response ->
                    if (response.code() == 202) {
                        _state.update {
                            it.copy(HasForked = true)
                        }
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                trySend(false)
                Log.d("ahi3646", "forkRepo: ${e.message} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "forkRepo: channel closed ")
        }
    }


    //getIssuesWithCount & getPullWithCount , they both uses same api
    fun getIssuesWithCount(token: String, query: String, page: Int, issueState: IssueState) {
        viewModelScope.launch {
            try {
                repository.getIssuesWithCount(token, query, page).let { issuesResponse ->
                    if (issuesResponse.isSuccessful) {
                        when (issueState) {
                            IssueState.Open -> {
                                _state.update {
                                    it.copy(
                                        OpenIssues = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }

                            IssueState.Closed -> {
                                _state.update {
                                    it.copy(
                                        ClosedIssues = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }

                            IssueState.All -> {
                                _state.update {
                                    it.copy(
                                        OpenIssues = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }
                        }
                    } else {
                        when (issueState) {
                            IssueState.Open -> {
                                _state.update {
                                    it.copy(
                                        OpenIssues = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }

                            IssueState.Closed -> {
                                _state.update {
                                    it.copy(
                                        ClosedIssues = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }

                            IssueState.All -> {
                                _state.update {
                                    it.copy(
                                        OpenIssues = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                when (issueState) {
                    IssueState.Open -> {
                        _state.update {
                            it.copy(
                                OpenIssues = Resource.Failure(e.message.toString())
                            )
                        }
                    }

                    IssueState.Closed -> {
                        _state.update {
                            it.copy(
                                ClosedIssues = Resource.Failure(e.message.toString())
                            )
                        }
                    }

                    IssueState.All -> {
                        _state.update {
                            it.copy(
                                OpenIssues = Resource.Failure(e.message.toString())
                            )
                        }
                    }
                }
            }
        }
    }

    fun getPullWithCount(token: String, query: String, page: Int, issueState: IssueState) {
        viewModelScope.launch {
            try {
                repository.getIssuesWithCount(token, query, page).let { issuesResponse ->
                    if (issuesResponse.isSuccessful) {
                        when (issueState) {
                            IssueState.Open -> {
                                _state.update {
                                    it.copy(
                                        OpenPulls = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }

                            IssueState.Closed -> {
                                _state.update {
                                    it.copy(
                                        ClosedPulls = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }

                            IssueState.All -> {
                                _state.update {
                                    it.copy(
                                        OpenPulls = Resource.Success(issuesResponse.body()!!)
                                    )
                                }
                            }
                        }
                    } else {
                        when (issueState) {
                            IssueState.Open -> {
                                _state.update {
                                    it.copy(
                                        OpenPulls = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }

                            IssueState.Closed -> {
                                _state.update {
                                    it.copy(
                                        ClosedPulls = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }

                            IssueState.All -> {
                                _state.update {
                                    it.copy(
                                        OpenPulls = Resource.Failure(
                                            issuesResponse.errorBody().toString()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                when (issueState) {
                    IssueState.Open -> {
                        _state.update {
                            it.copy(
                                OpenPulls = Resource.Failure(e.message.toString())
                            )
                        }
                    }

                    IssueState.Closed -> {
                        _state.update {
                            it.copy(
                                ClosedPulls = Resource.Failure(e.message.toString())
                            )
                        }
                    }

                    IssueState.All -> {
                        _state.update {
                            it.copy(
                                OpenPulls = Resource.Failure(e.message.toString())
                            )
                        }
                    }
                }
            }
        }
    }

    fun getReadMeMarkdown(token: String, repo: String, owner: String, branch: String) {
        viewModelScope.launch {
            try {
                repository.getReadMeMarkDown(token, owner, repo, branch).let { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(
                                HasMarkdown = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                HasMarkdown = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        HasMarkdown = false
                    )
                }
                Log.d("ahi3646", "getReadMeMarkdown: e - ${e.message} ")
            }
        }
    }

    fun getRepoIssues(
        token: String,
        repo: String,
        owner: String,
        state: String,
        sortBy: String,
        page: Int
    ) {
        viewModelScope.launch {
            try {
                repository.getRepoIssues(token, owner, repo, state, sortBy, page).let { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(
                                RepoIssues = Resource.Success(response.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                RepoIssues = Resource.Failure(response.errorBody().toString())
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        RepoIssues = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getLabels(token: String, owner: String, repo: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getLabels(token, owner, repo, page).let { labelsResponse ->
                    if (labelsResponse.isSuccessful) {
                        _state.update {
                            it.copy(Labels = Resource.Success(labelsResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Labels = Resource.Failure(
                                    labelsResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Labels = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getTags(token: String, owner: String, repo: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getTags(token, owner, repo, page).let { tagsModelResponse ->
                    if (tagsModelResponse.isSuccessful) {
                        _state.update {
                            it.copy(Tags = Resource.Success(tagsModelResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Tags = Resource.Failure(
                                    tagsModelResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Tags = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    private fun getLicense(token: String, license: String) {
        viewModelScope.launch {
            try {
                repository.getLicense(token, license).let { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(
                                License = Resource.Success(response.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                License = Resource.Failure(response.errorBody().toString())
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        License = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun updateFilesRef(ref: String) {
        _state.update {
            it.copy(FilesRef = ref)
        }
    }

    fun updateInitialBranch(ref: String) {
        _state.update {
            it.copy(InitialBranch = ref)
        }
    }

    fun updateCommitsRef(ref: String) {
        _state.update {
            it.copy(CommitsRef = ref)
        }
    }

    fun updatePaths(paths: List<String>) {
        _state.update {
            it.copy(
                Paths = paths
            )
        }
    }

    fun updateDownloadLink(link: Resource<String>) {
        _state.update {
            it.copy(
                RepoDownloadLink = link
            )
        }
    }

    fun setFields(owner: String, repo: String) {
        _state.update {
            it.copy(
                RepoOwner = owner,
                RepoName = repo
            )
        }
    }

}

data class RepositoryScreenState(
    val RepoOwner: String = "",
    val RepoName: String = "",
    val InitialBranch: String = "",

    val OpenIssues: Resource<IssuesModel> = Resource.Loading(),
    val ClosedIssues: Resource<IssuesModel> = Resource.Loading(),
    val OpenPulls: Resource<IssuesModel> = Resource.Loading(),
    val ClosedPulls: Resource<IssuesModel> = Resource.Loading(),

    val RepoIssues: Resource<RepoIssuesModel> = Resource.Loading(),
    val selectedBottomBarItem: RepositoryScreens = RepositoryScreens.Code,
    val Repository: Resource<RepoModel> = Resource.Loading(),
    val Contributors: Resource<Contributors> = Resource.Loading(),
    val Releases: Resource<ReleasesModel> = Resource.Loading(),
    val currentSheet: BottomSheetScreens = BottomSheetScreens.RepositoryInfoSheet,
    val HasMarkdown: Boolean = false,
    val RepositoryFiles: Resource<FilesModel> = Resource.Loading(),
    val Branches: Resource<BranchesModel> = Resource.Loading(),
    val Commits: Resource<CommitsModel> = Resource.Loading(),
    val isWatching: Boolean = false,
    val Watchers: Resource<SubscriptionsModel> = Resource.Loading(),
    val Stargazers: Resource<StargazersModel> = Resource.Loading(),
    val isStarring: Boolean = false,
    val Forks: Resource<ForksModel> = Resource.Loading(),
    val HasForked: Boolean = false,
    val License: Resource<LicenseResponse> = Resource.Loading(),
    val Labels: Resource<LabelsModel> = Resource.Loading(),
    val Tags: Resource<TagsModel> = Resource.Loading(),
    val Branch: Resource<BranchModel> = Resource.Loading(),
    val FilesRef: String = "main",
    val CommitsRef: String = "main",
    val Paths: List<String> = listOf(""),
    val RepoDownloadLink: Resource<String> = Resource.Loading()
)

sealed interface BottomSheetScreens {

    object RepositoryInfoSheet : BottomSheetScreens

    class ReleaseItemSheet(val releaseItem: ReleasesModelItem) : BottomSheetScreens

    class RepoDownloadSheet(val url: Resource<String>) : BottomSheetScreens

    object ForkSheet : BottomSheetScreens

}

interface RepositoryScreens {

    object Code : RepositoryScreens

    object Issues : RepositoryScreens

    object PullRequest : RepositoryScreens

    object Projects : RepositoryScreens

}