package com.rickandmorty.rickandmortymultimodule.screen.detail.detail_to_episode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.repository.CharacterEpisodeRepository
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
                CharacterEpisodeUiState.Error
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