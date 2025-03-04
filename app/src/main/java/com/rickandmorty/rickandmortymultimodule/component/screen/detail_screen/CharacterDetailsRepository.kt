package com.rickandmorty.rickandmortymultimodule.component.screen.detail_screen

import com.rickandmorty.network.ApiOperation
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import javax.inject.Inject

class CharacterDetailsRepository @Inject constructor(
    private val ktorClient: KtorClient,
){
    suspend fun fetchCharacterDetails(characterId : Int) : ApiOperation<Character> {
        return ktorClient.getCharacters(characterId)
    }
}
