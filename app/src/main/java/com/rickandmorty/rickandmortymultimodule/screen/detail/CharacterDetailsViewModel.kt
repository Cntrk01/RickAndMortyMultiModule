package com.rickandmorty.rickandmortymultimodule.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmortymultimodule.domain.repository.CharacterDetailsRepository
import com.rickandmortymultimodule.common.ui_elements.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterDetailsRepository: CharacterDetailsRepository,
) : ViewModel(){

    private val _internalStorageFlow = MutableStateFlow<CharacterDetailsViewState>(value = CharacterDetailsViewState.Loading)
    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacterDetails(characterId : Int) = viewModelScope.launch(Dispatchers.IO){
        _internalStorageFlow.update {
            return@update CharacterDetailsViewState.Loading
        }

        characterDetailsRepository
            .fetchCharacterDetails(characterId)
            .onSuccess { character ->
                val dataPoints = buildList {
                    add(
                        DataPoint(
                            title = "Last know location",
                            character.location.name
                        )
                    )
                    add(
                        DataPoint(
                            title = "Species",
                            character.species
                        )
                    )
                    add(
                        DataPoint(
                            title = "Gender",
                            character.gender.displayName
                        )
                    )
                    character.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint(title = "Type", type))
                    }
                    add(
                        DataPoint(
                            title = "Origin",
                            character.origin.name
                        )
                    )
                    add(
                        DataPoint(
                            title = "Episode count",
                            character.episodeIds.size.toString()
                        )
                    )
                }

                _internalStorageFlow.update {
                    return@update CharacterDetailsViewState.Success(
                        character = character,
                        dataPoints = dataPoints
                    )
                }
            }
            .onFailure { exception ->
                _internalStorageFlow.update {
                    return@update CharacterDetailsViewState.Error(
                        message = exception.message ?: "Unknown error occurred"
                    )
                }
            }
    }
}