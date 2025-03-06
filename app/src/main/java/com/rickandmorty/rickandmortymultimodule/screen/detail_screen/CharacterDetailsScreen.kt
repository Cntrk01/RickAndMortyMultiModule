package com.rickandmorty.rickandmortymultimodule.screen.detail_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterDetailsNamePlateComponent
import com.rickandmorty.rickandmortymultimodule.component.common.DataPointComponent
import com.rickandmorty.rickandmortymultimodule.component.common.LoadingState
import com.rickandmorty.rickandmortymultimodule.component.common.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.viewmodels.CharacterDetailsViewModel

@Composable
fun CharacterDetailsScreen(
    modifier: Modifier = Modifier,
    characterId: Int,
    viewModel: CharacterDetailsViewModel = hiltViewModel(),
    onEpisodeClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
){
    LaunchedEffect (Unit){
        viewModel.fetchCharacterDetails(characterId = characterId)
    }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    Column {
        SimpleToolbar(title = "Character Details",onBackAction = onBackClicked)
        LazyColumn (
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 16.dp)
        ){

            when(val viewState = state){
                CharacterDetailsViewState.Loading ->  item { LoadingState() }
                is CharacterDetailsViewState.Error -> {
                    // todo
                }
                is CharacterDetailsViewState.Success -> {
                    item{
                        CharacterDetailsNamePlateComponent(
                            name = viewState.character.name,
                            status = viewState.character.status
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }


                    item {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            model = viewState.character.imageUrl,
                            loading = {
                                LoadingState()
                            },
                            contentDescription = "Character Image")
                    }

                    items(viewState.dataPoints){
                        Spacer(modifier = Modifier.height(32.dp))
                        DataPointComponent(dataPoint = it)
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }

                    item {
                        Text(
                            text = "View all episodes",
                            color = RickAction,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .border(
                                    width = 1.dp,
                                    color = RickAction,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onEpisodeClicked(characterId)
                                }
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                        )
                    }

                    item { Spacer(modifier = Modifier.height(64.dp)) }
                }
            }
        }
    }
}