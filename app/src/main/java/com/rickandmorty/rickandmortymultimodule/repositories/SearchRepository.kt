package com.rickandmorty.rickandmortymultimodule.repositories

import com.rickandmorty.network.ApiOperation
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.CharacterPage
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val ktorClient: KtorClient,
) {
    suspend fun getCharacterByPage(
        pageNumber: Int,
        queryParams: Map<String, String>
    ): ApiOperation<CharacterPage> = ktorClient.getCharacterByPage(pageNumber = pageNumber, queryParams = queryParams)

    suspend fun getAllCharactersByName(
        searchQuery: String
    ): ApiOperation<List<Character>> = ktorClient.searchAllCharactersByName(searchQuery = searchQuery)
}