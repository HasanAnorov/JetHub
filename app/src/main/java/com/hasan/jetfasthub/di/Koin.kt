package com.hasan.jetfasthub.di

import com.hasan.jetfasthub.data.AuthRepository
import com.hasan.jetfasthub.data.AuthRepositoryImpl
import com.hasan.jetfasthub.screens.login.LoginViewModel
import com.hasan.jetfasthub.screens.login.basic_auth.BasicAuthViewModel
import com.hasan.jetfasthub.screens.main.home.HomeRepository
import com.hasan.jetfasthub.screens.main.home.HomeRepositoryImpl
import com.hasan.jetfasthub.screens.main.home.HomeViewModel
import com.hasan.jetfasthub.screens.main.home.events.EventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }
}

val eventsModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    viewModel{ EventsViewModel(get()) }
}

val basicAuthViewModelModule = module {
    viewModel { BasicAuthViewModel() }
}

val homeViewModelModule = module {
    viewModelOf(::HomeViewModel)
}