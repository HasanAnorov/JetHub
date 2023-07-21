package com.hasan.jetfasthub.di

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.hasan.jetfasthub.data.AuthRepository
import com.hasan.jetfasthub.data.AuthRepositoryImpl
import com.hasan.jetfasthub.data.GistsRepository
import com.hasan.jetfasthub.data.GistsRepositoryImpl
import com.hasan.jetfasthub.screens.login.LoginViewModel
import com.hasan.jetfasthub.screens.login.basic_auth.BasicAuthViewModel
import com.hasan.jetfasthub.data.HomeRepository
import com.hasan.jetfasthub.data.HomeRepositoryImpl
import com.hasan.jetfasthub.data.NotificationRepository
import com.hasan.jetfasthub.data.NotificationsRepositoryImpl
import com.hasan.jetfasthub.data.OrganisationImpl
import com.hasan.jetfasthub.data.OrganisationRepository
import com.hasan.jetfasthub.data.ProfileRepository
import com.hasan.jetfasthub.data.ProfileRepositoryImpl
import com.hasan.jetfasthub.data.Repository
import com.hasan.jetfasthub.data.RepositoryImpl
import com.hasan.jetfasthub.data.SearchRepository
import com.hasan.jetfasthub.data.SearchRepositoryImpl
import com.hasan.jetfasthub.networking.AuthInterceptor
import com.hasan.jetfasthub.networking.GitHubService
import com.hasan.jetfasthub.screens.main.gists.GistsViewModel
import com.hasan.jetfasthub.screens.main.home.HomeViewModel
import com.hasan.jetfasthub.screens.main.notifications.NotificationsViewModel
import com.hasan.jetfasthub.screens.main.organisations.OrganisationsViewModel
import com.hasan.jetfasthub.screens.main.profile.ProfileViewModel
import com.hasan.jetfasthub.screens.main.repository.RepositoryViewModel
import com.hasan.jetfasthub.screens.main.search.SearchViewModel
import com.hasan.jetfasthub.utility.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }
}

val networkModule = module {
    factory { AuthInterceptor(get()) }
    factory {
        provideOkHttpClient(
            ChuckerInterceptor.Builder(get())
                .collector(ChuckerCollector(get()))
                .maxContentLength(250000L)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build(),
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        )
    }
    factory { provideGitHubService(get()) }
    single { provideRetrofit(get()) }
}

fun provideGitHubService(retrofit: Retrofit): GitHubService {
    return retrofit.create(GitHubService::class.java)
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
}

fun provideOkHttpClient(
    chuckerInterceptor: ChuckerInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder().addInterceptor(chuckerInterceptor)
        .addInterceptor(httpLoggingInterceptor).build()
}

val profileModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    viewModel { ProfileViewModel(get()) }
}

val organisationModule = module {
    single<OrganisationRepository>{OrganisationImpl(get())}
    viewModel{ OrganisationsViewModel(get()) }
}

val gistsModule = module {
    single<GistsRepository> { GistsRepositoryImpl(get()) }
    viewModel{ GistsViewModel(get()) }
}

val eventsModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    viewModel { HomeViewModel(get()) }
}

val repositoryModule = module {
    single <Repository>{ RepositoryImpl(get()) }
    viewModel { RepositoryViewModel(get()) }
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

val homeViewModelModule = module {
    viewModelOf(::HomeViewModel)
}