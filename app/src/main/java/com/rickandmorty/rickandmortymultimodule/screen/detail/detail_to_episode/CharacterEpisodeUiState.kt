package com.rickandmorty.rickandmortymultimodule.screen.detail.detail_to_episode

import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.Episode

sealed interface CharacterEpisodeUiState {
    data object Error : CharacterEpisodeUiState
    data object Loading : CharacterEpisodeUiState
    data class Success(
        val character: Character?,
        val data: List<Episode>,
    ) : CharacterEpisodeUiState
}