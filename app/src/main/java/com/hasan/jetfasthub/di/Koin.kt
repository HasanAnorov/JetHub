package com.hasan.jetfasthub.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.hasan.jetfasthub.data.AuthRepository
import com.hasan.jetfasthub.data.AuthRepositoryImpl
import com.hasan.jetfasthub.data.CommentRepository
import com.hasan.jetfasthub.data.CommentRepositoryImpl
import com.hasan.jetfasthub.data.CommitRepository
import com.hasan.jetfasthub.data.CommitRepositoryImpl
import com.hasan.jetfasthub.data.FileViewRepository
import com.hasan.jetfasthub.data.FileViewRepositoryImpl
import com.hasan.jetfasthub.data.GistRepository
import com.hasan.jetfasthub.data.GistRepositoryImpl
import com.hasan.jetfasthub.data.GistsRepository
import com.hasan.jetfasthub.data.GistsRepositoryImpl
import com.hasan.jetfasthub.screens.login.LoginViewModel
import com.hasan.jetfasthub.screens.login.basic_auth.BasicAuthViewModel
import com.hasan.jetfasthub.data.HomeRepository
import com.hasan.jetfasthub.data.HomeRepositoryImpl
import com.hasan.jetfasthub.data.IssueRepository
import com.hasan.jetfasthub.data.IssueRepositoryImpl
import com.hasan.jetfasthub.data.NotificationRepository
import com.hasan.jetfasthub.data.NotificationsRepositoryImpl
import com.hasan.jetfasthub.data.OrganisationImpl
import com.hasan.jetfasthub.data.OrganisationRepository
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.data.ProfileRepository
import com.hasan.jetfasthub.data.ProfileRepositoryImpl
import com.hasan.jetfasthub.data.Repository
import com.hasan.jetfasthub.data.RepositoryImpl
import com.hasan.jetfasthub.data.SearchRepository
import com.hasan.jetfasthub.data.SearchRepositoryImpl
import com.hasan.jetfasthub.data.download.AndroidDownloader
import com.hasan.jetfasthub.data.download.Downloader
import com.hasan.jetfasthub.screens.main.commits.CommitViewModel
import com.hasan.jetfasthub.screens.main.commits.EditCommentViewModel
import com.hasan.jetfasthub.screens.main.file_view.FileViewVM
import com.hasan.jetfasthub.screens.main.gists.GistViewModel
import com.hasan.jetfasthub.screens.main.gists.GistsViewModel
import com.hasan.jetfasthub.screens.main.home.HomeViewModel
import com.hasan.jetfasthub.screens.main.home.data.local.HomeDatabase
import com.hasan.jetfasthub.screens.main.home.data.local.ReceivedEventsModelEntity
import com.hasan.jetfasthub.screens.main.home.data.remote.EventsRemoteMediator
import com.hasan.jetfasthub.screens.main.issue.IssueViewModel
import com.hasan.jetfasthub.screens.main.notifications.NotificationsViewModel
import com.hasan.jetfasthub.screens.main.organisations.OrganisationsViewModel
import com.hasan.jetfasthub.screens.main.profile.ProfileViewModel
import com.hasan.jetfasthub.screens.main.repository.RepositoryViewModel
import com.hasan.jetfasthub.screens.main.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }
}

val fileViewModule = module {
    single<Downloader> { AndroidDownloader(get()) }
    single<FileViewRepository> { FileViewRepositoryImpl(get()) }
    viewModel{ FileViewVM(get(), get()) }
}

val profileModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    viewModel { ProfileViewModel(get()) }
}

val organisationModule = module {
    single<OrganisationRepository> { OrganisationImpl(get()) }
    viewModel { OrganisationsViewModel(get()) }
}

val gistsModule = module {
    single<GistsRepository> { GistsRepositoryImpl(get()) }
    viewModel { GistsViewModel(get()) }
}

val gistModule = module {
    single<GistRepository> { GistRepositoryImpl(get()) }
    viewModel { GistViewModel(get()) }
}

@OptIn(ExperimentalPagingApi::class)
val homeModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single {
        Room.databaseBuilder(
            androidApplication(),
            HomeDatabase::class.java,
            "events.db"
        ).build()
    }
    single { PreferenceHelper.getToken(androidApplication()) }
    single { PreferenceHelper.getAuthenticatedUsername(androidApplication()) }
    single { EventsRemoteMediator(get(), get(), get(), get()) }

    fun providesEventPager(
        eventsDb: HomeDatabase,
        eventApi: HomeRepository,
        token: String,
        username: String
    ): Pager<Int, ReceivedEventsModelEntity> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            remoteMediator = EventsRemoteMediator(
                repository = eventApi,
                homeDatabase = eventsDb,
                token = token,
                username = username
            ),
            pagingSourceFactory = {
                eventsDb.dao.pagingSource()
            }
        )
    }

    viewModel { HomeViewModel(get(), providesEventPager(get(), get(), get(), get())) }
}

val issueModule = module {
    single<IssueRepository> { IssueRepositoryImpl(get()) }
    viewModel { IssueViewModel(get()) }
}

val repositoryModule = module {
    single<Downloader> { AndroidDownloader(get()) }
    single<Repository> { RepositoryImpl(get()) }
    viewModel { RepositoryViewModel(get(), get()) }
}

val commitModule = module {
    single<Downloader> { AndroidDownloader(get()) }
    single<CommitRepository> { CommitRepositoryImpl(get()) }
    viewModel { CommitViewModel(get(), get()) }
}

val commentEditModule = module {
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    viewModel { EditCommentViewModel(get()) }
}

val notificationsModule = module {
    single<NotificationRepository> { NotificationsRepositoryImpl(get()) }
    viewModel { NotificationsViewModel(get()) }
}

val searchModule = module {
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    viewModel { SearchViewModel(get()) }
}

val basicAuthViewModelModule = module {
    viewModel { BasicAuthViewModel() }
}