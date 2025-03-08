package com.rickandmorty.rickandmortymultimodule.repositories

import com.rickandmorty.network.ApiOperation
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Episode
import javax.inject.Inject

class AllEpisodesRepository @Inject constructor(
    private val client: KtorClient,
) {

    suspend fun getAllEpisodes() : ApiOperation<List<Episode>> = client.getAllEpisodes()
}