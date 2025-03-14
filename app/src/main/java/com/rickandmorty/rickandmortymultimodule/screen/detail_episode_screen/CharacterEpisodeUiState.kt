package com.rickandmorty.rickandmortymultimodule.screen.detail_episode_screen

import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.Episode

sealed interface CharacterEpisodeUiState {
    data object Error : CharacterEpisodeUiState
    data object Loading : CharacterEpisodeUiState
    data class Success(
        val character: Character?,
        val data: List<Episode>,
    ) : CharacterEpisodeUiState
}