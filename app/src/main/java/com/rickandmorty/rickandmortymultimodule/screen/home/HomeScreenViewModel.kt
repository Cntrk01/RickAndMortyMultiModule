package com.rickandmorty.rickandmortymultimodule.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmortymultimodule.domain.model.CharacterPage
import com.rickandmortymultimodule.domain.repository.CharacterHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val characterHomeRepository: CharacterHomeRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val viewState: StateFlow<HomeScreenUiState> = _viewState.asStateFlow()

    private val fetchedCharacters = mutableListOf<CharacterPage>()

    fun fetchInitialPage() = viewModelScope.launch(Dispatchers.IO) {
        if (fetchedCharacters.isNotEmpty()) return@launch // detay sayfasına gidip dönünce tekrar bu blok çalıştığı için state koruyamıyorduk.Eğer boş değilse buraya girmeden çıkmasını istiyorum.
        val initialPage = characterHomeRepository.fetchCharacterPage(page = 1)
        initialPage
            .onSuccess { characterPage ->
                fetchedCharacters.clear()
                fetchedCharacters.add(characterPage)
                _viewState.update {
                    return@update HomeScreenUiState.GridDisplay(
                        characters = characterPage.characters
                    )
                }
            }.onFailure {
                // todo
            }
    }

    fun fetchNextPage() {
        val nextPageInList = fetchedCharacters.size + 1

        viewModelScope.launch(Dispatchers.IO) {
            characterHomeRepository
                .fetchCharacterPage(page = nextPageInList)
                .onSuccess { character ->
                    fetchedCharacters.add(character)
                    _viewState.update { currentState ->
                        val currentCharacter =
                            (currentState as? HomeScreenUiState.GridDisplay)?.characters
                                ?: emptyList()

                        return@update HomeScreenUiState.GridDisplay(
                            characters = currentCharacter + character.characters
                        )
                    }
                }.onFailure {

                }
        }
    }
}