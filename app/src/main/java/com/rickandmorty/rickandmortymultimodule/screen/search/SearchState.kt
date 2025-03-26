package com.rickandmorty.rickandmortymultimodule.screen.search

sealed interface  SearchState{
    data object Empty : SearchState
    data class UserQuery(val query : String) : SearchState
}