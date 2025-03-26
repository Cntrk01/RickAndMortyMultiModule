package com.rickandmortymultimodule.domain.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.Episode

interface CharacterEpisodeRepository {
    suspend fun getCharacters(characterId: Int): ApiOperation<Character>

    suspend fun getEpisodes(episodeIds: List<Int>): ApiOperation<List<Episode>>
}