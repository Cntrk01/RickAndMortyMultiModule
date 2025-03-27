package com.rickandmortymultimodule.data.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.mappers.toDomainEpisodes
import com.rickandmortymultimodule.data.service.KtorClient
import com.rickandmortymultimodule.domain.model.Episode
import com.rickandmortymultimodule.domain.repository.AllEpisodesRepository
import javax.inject.Inject

class AllEpisodesRepositoryImpl @Inject constructor(
    private val ktorClient: KtorClient,
) : AllEpisodesRepository {
    override suspend fun getAllEpisodes(): ApiOperation<List<Episode>> {
        return ktorClient
            .getAllEpisodes()
            .mapSuccess {
                it.toDomainEpisodes()
        }
    }
}