package com.rickandmortymultimodule.data.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.mappers.toDomainCharacter
import com.rickandmortymultimodule.data.mappers.toDomainEpisodes
import com.rickandmortymultimodule.data.service.KtorClient
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.Episode
import com.rickandmortymultimodule.domain.repository.CharacterEpisodeRepository
import javax.inject.Inject

class CharacterEpisodeRepositoryImpl  @Inject constructor(
    private val ktorClient: KtorClient,
) : CharacterEpisodeRepository {
    override suspend fun getCharacters(characterId: Int): ApiOperation<Character> {
        return ktorClient
            .getCharacters(characterId)
            .mapSuccess {
                it.toDomainCharacter()
            }
    }

    override suspend fun getEpisodes(episodeIds: List<Int>): ApiOperation<List<Episode>> {
        return ktorClient
            .getEpisodes(episodeIds) //ApiOperation<List<RemoteEpisode>>
            .mapSuccess { //it = List<RemoteEpisode>
                it.toDomainEpisodes() //List<Episode>
            }
    }
}