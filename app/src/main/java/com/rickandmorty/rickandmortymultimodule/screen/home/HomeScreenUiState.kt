package com.rickandmorty.rickandmortymultimodule.screen.home

import com.rickandmortymultimodule.domain.model.Character

sealed interface HomeScreenUiState{
    data object Loading : HomeScreenUiState
    data class GridDisplay(
        val characters : List<Character> = emptyList()
    ) : HomeScreenUiState
}