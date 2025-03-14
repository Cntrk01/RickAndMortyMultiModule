package com.rickandmorty.rickandmortymultimodule.repositories

import com.rickandmorty.network.ApiOperation
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.Episode
import javax.inject.Inject

class CharacterEpisodeRepository  @Inject constructor(
    private val ktorClient: KtorClient,
) {
    suspend fun getCharacters(characterId: Int): ApiOperation<Character> {
        return ktorClient.getCharacters(characterId)
    }

    suspend fun getEpisodes(episodeIds: List<Int>): ApiOperation<List<Episode>> {
        return ktorClient.getEpisodes(episodeIds)
    }
}