package com.rickandmorty.rickandmortymultimodule.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.Episode
import com.rickandmorty.rickandmortymultimodule.component.common.CharacterImage
import com.rickandmorty.rickandmortymultimodule.component.common.CharacterNameComponent
import com.rickandmorty.rickandmortymultimodule.component.common.DataPoint
import com.rickandmorty.rickandmortymultimodule.component.common.DataPointComponent
import com.rickandmorty.rickandmortymultimodule.component.common.LoadingState
import com.rickandmorty.rickandmortymultimodule.component.episode.EpisodeRowComponent
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickTextPrimary
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodeScreen(
    characterId : Int,
    ktorClient: KtorClient
){
  var characterState by remember {
      mutableStateOf<Character?>(null)
  }
  var episodesState by remember {
      mutableStateOf<List<Episode>>(emptyList())
  }

  LaunchedEffect (Unit){
      ktorClient
          .getCharacters(characterId)
          .onSuccess { character ->
            characterState = character

              launch {
                  ktorClient
                      .getEpisodes(character.episodeIds)
                      .onSuccess { episodeList->
                          episodesState = episodeList
                      }
                      .onFailure {
                          //TODO: Hata durumunda ne yapılacak
                      }
              }
          }
          .onFailure {
            //TODO: Hata durumunda ne yapılacak
          }
  }

    characterState?.let {
        MainScreen(
            character = it,
            episodes = episodesState,
        )
    } ?: LoadingState()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(
    character: Character,
    episodes : List<Episode> = emptyList(),
){
    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }

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
                        DataPointComponent(dataPoint = DataPoint(title = title,description = description))
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