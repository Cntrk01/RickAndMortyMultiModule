package com.rickandmorty.rickandmortymultimodule.screen.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterGridItem
import com.rickandmorty.rickandmortymultimodule.component.common.LoadingState
import com.rickandmorty.rickandmortymultimodule.repositories.CharacterHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeScreenViewState{
    data object Loading : HomeScreenViewState
    data class GridDisplay(
        val characters : List<Character> = emptyList()
    ) : HomeScreenViewState
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val characterHomeRepository: CharacterHomeRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow<HomeScreenViewState>(HomeScreenViewState.Loading)
    val viewState: StateFlow<HomeScreenViewState> = _viewState.asStateFlow()

    fun fetchInitialPage() = viewModelScope.launch(Dispatchers.IO) {
        val initialPage = characterHomeRepository.fetchCharacterPage(page = 1)
        initialPage.onSuccess { characterPage ->
            _viewState.update {
                return@update HomeScreenViewState.GridDisplay(
                    characters = characterPage.characters
                )
            }
        }.onFailure {
            // todo
        }

        fun fetchNextPage() {

        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    onCharacterSelected : (Int) -> Unit,
) {
    val viewState by homeScreenViewModel.viewState.collectAsStateWithLifecycle()

    val scrollState = rememberLazyGridState()

    LaunchedEffect (Unit){
        homeScreenViewModel.fetchInitialPage()
    }

    when(val state = viewState){
        HomeScreenViewState.Loading -> LoadingState()
        is HomeScreenViewState.GridDisplay -> {
            Column (modifier = modifier){
                //SimpleToolbar(title = "All characters")
                LazyVerticalGrid(
                    state = scrollState,
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(2),
                    content = {
                        items(
                            items = state.characters,
                            key = { it.id }
                        ) { character ->
                            CharacterGridItem(modifier = Modifier, character = character) {
                                onCharacterSelected(character.id)
                            }
                        }
                    })
            }
        }
        }
}