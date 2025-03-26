package com.rickandmortymultimodule.domain.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.domain.model.CharacterPage

interface CharacterHomeRepository {
    suspend fun fetchCharacterPage(page: Int): ApiOperation<CharacterPage>
}