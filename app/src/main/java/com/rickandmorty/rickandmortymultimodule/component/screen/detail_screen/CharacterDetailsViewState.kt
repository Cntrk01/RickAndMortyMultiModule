package com.rickandmorty.rickandmortymultimodule.component.screen.detail_screen

import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.rickandmortymultimodule.component.common.DataPoint


sealed interface CharacterDetailsViewState{
    data object Loading : CharacterDetailsViewState
    data class Error(val message : String) : CharacterDetailsViewState
    data class Success(
        val character : Character,
        val dataPoints : List<DataPoint>
    ) : CharacterDetailsViewState
}
