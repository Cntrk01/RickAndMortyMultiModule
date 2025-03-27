package com.rickandmorty.rickandmortymultimodule.screen.search

import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.CharacterStatus

sealed interface ScreenState{
    data object Empty : ScreenState
    data object Searching : ScreenState
    data class Error(val message : String) : ScreenState
    data class Content(
        val userQuery : String,
        val results : List<Character>,
        val filterState : FilterState,
    ) : ScreenState{
        data class FilterState(
            val statuses : List<CharacterStatus>,
            val selectedStatuses : List<CharacterStatus>, //seçilmiş olan statüleri temsil ediyor
        )
    }
}