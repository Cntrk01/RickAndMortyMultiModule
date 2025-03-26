package com.rickandmortymultimodule.domain.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.domain.model.Character

interface SearchRepository {
    suspend fun getAllCharactersByName(
        searchQuery: String
    ): ApiOperation<List<Character>>
}