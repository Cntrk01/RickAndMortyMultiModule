package com.rickandmorty.rickandmortymultimodule.screen.all_episode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rickandmortymultimodule.common.ui_elements.LoadingState
import com.rickandmortymultimodule.common.ui_elements.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.component.episode.EpisodeRowComponent
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllEpisodeScreen(
    modifier: Modifier = Modifier,
    episodesViewModel: AllEpisodesViewModel = hiltViewModel()
) {
    val uiState by episodesViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        episodesViewModel.refreshAllEpisodes()
    }

    when (val state = uiState) {
        AllEpisodesUiState.Error -> {
            // todo
        }

        AllEpisodesUiState.Loading -> LoadingState()
        is AllEpisodesUiState.Success -> {
            Column (
                modifier = modifier
            ){
                SimpleToolbar(title = "All episodes")
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.data.forEach { mapEntry ->
                        println(mapEntry.key)
                        stickyHeader(key = mapEntry.key) {
                            Header(
                                seasonName = mapEntry.key,
                                uniqueCharacterCount = mapEntry.value.flatMap {
                                    it.characterIdsInEpisode
                                }.toSet().size //benzersiz karakter sayısını aldık.
                            )
                        }

                        mapEntry.value.forEach { episode ->
                            item(key = episode.id) { EpisodeRowComponent(episode = episode) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(seasonName: String, uniqueCharacterCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
    ) {
        Text(text = seasonName, color = Color.White, fontSize = 32.sp)
        Text(
            text = "$uniqueCharacterCount unique characters",
            color = Color.White,
            fontSize = 22.sp,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(4.dp)
                .background(
                    color = RickAction,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}