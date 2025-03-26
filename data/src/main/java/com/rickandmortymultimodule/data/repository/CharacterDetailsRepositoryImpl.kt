package com.rickandmortymultimodule.data.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.mappers.toDomainCharacter
import com.rickandmortymultimodule.data.service.KtorClient
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.repository.CharacterDetailsRepository
import javax.inject.Inject

class CharacterDetailsRepositoryImpl @Inject constructor(
    private val ktorClient: KtorClient,
) : CharacterDetailsRepository {
    override suspend fun fetchCharacterDetails(characterId : Int) : ApiOperation<Character> {
        return ktorClient
            .getCharacters(characterId)
            .mapSuccess { it.toDomainCharacter() }
    }
}
