package com.rickandmorty.rickandmortymultimodule.screen.detail

import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.common.ui_elements.DataPoint


sealed interface CharacterDetailsViewState{
    data object Loading : CharacterDetailsViewState
    data class Error(val message : String) : CharacterDetailsViewState
    data class Success(
        val character : Character,
        val dataPoints : List<DataPoint>
    ) : CharacterDetailsViewState
}
