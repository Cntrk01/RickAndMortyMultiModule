package com.rickandmorty.rickandmortymultimodule.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterGridItem
import com.rickandmortymultimodule.common.ui_elements.LoadingState
import com.rickandmortymultimodule.common.ui_elements.SimpleToolbar

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

    val fetchNextPage : Boolean by remember {
        derivedStateOf {
            val currentCharacterCount = (viewState as? HomeScreenUiState.GridDisplay)?.characters?.size ?: return@derivedStateOf false

            val lastDisplayedIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false

            return@derivedStateOf lastDisplayedIndex >= currentCharacterCount - 10
        }
    }

    LaunchedEffect(fetchNextPage){
        if (fetchNextPage){
            homeScreenViewModel.fetchNextPage()
        }
    }

    when(val state = viewState){
        HomeScreenUiState.Loading -> LoadingState()
        is HomeScreenUiState.GridDisplay -> {
            Column (modifier = modifier){
                SimpleToolbar(title = "All characters")
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
