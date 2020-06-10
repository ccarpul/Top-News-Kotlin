package com.example.topnewsmvvmkotlin.data.di

import com.example.topnewsmvvmkotlin.data.getApiService
import com.example.topnewsmvvmkotlin.ui.home.HomeRepository
import com.example.topnewsmvvmkotlin.ui.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

//modulo para inyeccion de dependencias con Koin
//este módulo permitirá agregar la dependencia de HomeViewModel en el MainActivity
val injectionDependencyModule = module {
    viewModel { HomeViewModel(get()) }
    single { HomeRepository(get()) }
    single { getApiService()}
}

