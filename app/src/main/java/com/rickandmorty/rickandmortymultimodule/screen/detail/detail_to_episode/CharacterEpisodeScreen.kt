package com.rickandmorty.rickandmortymultimodule.screen.detail.detail_to_episode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.Episode
import com.rickandmortymultimodule.common.ui_elements.CharacterImage
import com.rickandmortymultimodule.common.ui_elements.CharacterNameComponent
import com.rickandmortymultimodule.common.ui_elements.DataPoint
import com.rickandmortymultimodule.common.ui_elements.DataPointComponent
import com.rickandmortymultimodule.common.ui_elements.LoadingState
import com.rickandmortymultimodule.common.ui_elements.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.component.episode.EpisodeRowComponent
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickTextPrimary

@Composable
fun CharacterEpisodeScreen(
    characterId : Int,
    onBackAction: (() -> Unit)?,
    viewModel : CharacterEpisodeViewModel = hiltViewModel(),
){
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect (Unit){
      viewModel.getCharacterEpisodes(characterId = characterId)
  }

  when(uiState){
      is CharacterEpisodeUiState.Error -> Text(text = "Not Found Episode")
      is CharacterEpisodeUiState.Loading -> LoadingState()
      is CharacterEpisodeUiState.Success ->
          with((uiState as CharacterEpisodeUiState.Success)){
              character?.let {
                  MainScreen(
                      character = it,
                      episodes = data,
                      onBackAction = onBackAction,
                  )
          }
      }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(
    character: Character,
    episodes : List<Episode> = emptyList(),
    onBackAction: (() -> Unit)? = null
){
    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }

    Column {
        SimpleToolbar(
            title = "Episodes",
            onBackAction = onBackAction
        )
        LazyColumn (
            modifier = Modifier.padding(all = 16.dp)
        ){
            item { CharacterNameComponent(name = character.name) }
            item { Spacer(modifier = Modifier.height(16.dp))}
            item {
                LazyRow {
                    episodeBySeasonMap.forEach {
                        val title = "Season ${it.key}"
                        val description = "${it.value.size} ep"
                        item {
                            DataPointComponent(
                                dataPoint = DataPoint(
                                    title = title,
                                    description = description
                                )
                            )
                            Spacer(modifier = Modifier.width(32.dp))
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp))}

            item { CharacterImage(imageUrl = character.imageUrl) }

            item { Spacer(modifier = Modifier.height(16.dp))}

            episodeBySeasonMap.forEach { mapEntry ->
                //Başlık (header) bileşeninin, liste içinde kaydırılırken üstte sabit kalmasını (yapışmasını) sağlar.
                stickyHeader { SeasonHeader(seasonNumber = mapEntry.key) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(mapEntry.value){ episode ->
                    EpisodeRowComponent(episode = episode)
                }
            }
        }
    }
}

@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 4.dp)
        )
    }
}