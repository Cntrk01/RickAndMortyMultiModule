package com.rickandmorty.rickandmortymultimodule.screen.all_episode_screen

import com.rickandmorty.network.models.domain.Episode

sealed interface AllEpisodesUiState {
    data object Error : AllEpisodesUiState
    data object Loading : AllEpisodesUiState
    data class Success(val data: Map<String, List<Episode>>) : AllEpisodesUiState
}