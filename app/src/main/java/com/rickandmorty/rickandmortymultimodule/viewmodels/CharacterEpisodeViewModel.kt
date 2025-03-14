package com.rickandmorty.rickandmortymultimodule.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.rickandmortymultimodule.repositories.CharacterEpisodeRepository
import com.rickandmorty.rickandmortymultimodule.screen.detail_episode_screen.CharacterEpisodeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterEpisodeViewModel @Inject constructor(
    private var characterEpisodeRepository: CharacterEpisodeRepository
) : ViewModel() {

    private var character by mutableStateOf<Character?>(null)

    private val _uiState = MutableStateFlow<CharacterEpisodeUiState>(CharacterEpisodeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getCharacterEpisodes(characterId: Int) = viewModelScope.launch (Dispatchers.IO){
        characterEpisodeRepository.getCharacters(characterId)
            .onSuccess { charater ->
                character = charater
                getEpisodes(charater.episodeIds)
            }
            .onFailure {

            }
    }

    private fun getEpisodes(episodeIds: List<Int>) = viewModelScope.launch (Dispatchers.IO){
        characterEpisodeRepository.getEpisodes(episodeIds)
            .onSuccess { episodes ->
                _uiState.update {
                    CharacterEpisodeUiState.Success(
                        character = character,
                        data = episodes,
                    )
                }
            }
            .onFailure {
                CharacterEpisodeUiState.Error
            }
    }
}