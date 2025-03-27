package com.rickandmortymultimodule.data.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.mappers.toDomainCharacter
import com.rickandmortymultimodule.data.service.KtorClient
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val ktorClient: KtorClient,
) : SearchRepository {
    override suspend fun getAllCharactersByName(
        searchQuery: String
    ): ApiOperation<List<Character>> =
        ktorClient
            .searchAllCharactersByName(searchQuery = searchQuery)
            .mapSuccess {
                it.map { remoteCharacter ->
                    remoteCharacter.toDomainCharacter()
                }
            }
}