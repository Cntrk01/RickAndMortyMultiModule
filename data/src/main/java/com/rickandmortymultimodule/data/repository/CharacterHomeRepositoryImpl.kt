package com.rickandmortymultimodule.data.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.mappers.toDomainCharacterPage
import com.rickandmortymultimodule.data.service.KtorClient
import com.rickandmortymultimodule.domain.model.CharacterPage
import com.rickandmortymultimodule.domain.repository.CharacterHomeRepository
import javax.inject.Inject

class CharacterHomeRepositoryImpl @Inject constructor(
    private val ktorClient: KtorClient,
) : CharacterHomeRepository {
    override suspend fun fetchCharacterPage(page: Int): ApiOperation<CharacterPage> {
        return ktorClient
            .getCharacterByPage(page)
            .mapSuccess {
                it.toDomainCharacterPage()
        }
    }
}