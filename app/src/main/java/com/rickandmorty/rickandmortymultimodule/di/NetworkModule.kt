package com.rickandmorty.rickandmortymultimodule.di

import com.rickandmorty.network.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient() : KtorClient{
        return KtorClient()
    }
}