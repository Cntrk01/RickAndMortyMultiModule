package com.rickandmortymultimodule.domain.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.domain.model.Character

interface CharacterDetailsRepository {
    suspend fun fetchCharacterDetails(characterId : Int) : ApiOperation<Character>
}