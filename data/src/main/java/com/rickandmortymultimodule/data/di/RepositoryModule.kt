package com.rickandmortymultimodule.data.di

import com.rickandmortymultimodule.data.repository.AllEpisodesRepositoryImpl
import com.rickandmortymultimodule.data.repository.CharacterDetailsRepositoryImpl
import com.rickandmortymultimodule.data.repository.CharacterEpisodeRepositoryImpl
import com.rickandmortymultimodule.data.repository.CharacterHomeRepositoryImpl
import com.rickandmortymultimodule.data.repository.SearchRepositoryImpl
import com.rickandmortymultimodule.domain.repository.AllEpisodesRepository
import com.rickandmortymultimodule.domain.repository.CharacterDetailsRepository
import com.rickandmortymultimodule.domain.repository.CharacterEpisodeRepository
import com.rickandmortymultimodule.domain.repository.CharacterHomeRepository
import com.rickandmortymultimodule.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAllEpisodesRepository(
        impl: AllEpisodesRepositoryImpl
    ): AllEpisodesRepository

    @Binds
    abstract fun bindDetailsRepository(
        impl: CharacterDetailsRepositoryImpl
    ) : CharacterDetailsRepository

    @Binds
    abstract fun bindEpisodeRepository(
        impl: CharacterEpisodeRepositoryImpl
    ) : CharacterEpisodeRepository

    @Binds
    abstract fun bindCharacterHomeRepository(
        impl: CharacterHomeRepositoryImpl
    ): CharacterHomeRepository

    @Binds
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ) : SearchRepository
}