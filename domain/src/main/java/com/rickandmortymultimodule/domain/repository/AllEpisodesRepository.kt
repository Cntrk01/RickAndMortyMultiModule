package com.rickandmortymultimodule.domain.repository

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.domain.model.Episode

interface AllEpisodesRepository {
    suspend fun getAllEpisodes() : ApiOperation<List<Episode>>
}