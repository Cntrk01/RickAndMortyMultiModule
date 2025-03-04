package com.rickandmorty.rickandmortymultimodule.repositories

import com.rickandmorty.network.ApiOperation
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.CharacterPage
import javax.inject.Inject

class CharacterHomeRepository @Inject constructor(
    private val ktorClient: KtorClient,
) {
    suspend fun fetchCharacterPage(page: Int): ApiOperation<CharacterPage> {
        return ktorClient.getCharacterByPage(page)
    }
}