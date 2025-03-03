package com.rickandmorty.rickandmortymultimodule.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterDetailsNamePlateComponent
import com.rickandmorty.rickandmortymultimodule.component.common.DataPoint
import com.rickandmorty.rickandmortymultimodule.component.common.DataPointComponent
import com.rickandmorty.rickandmortymultimodule.component.common.LoadingState
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import kotlinx.coroutines.delay

@Composable
fun CharacterDetailsScreen(
    modifier: Modifier = Modifier,
    ktorClient: KtorClient,
    characterId : Int,
    onEpisodeClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
){
    var character by remember {
        mutableStateOf<Character?>(null)
    }

    val characterDataPoints by remember {
        derivedStateOf {
            buildList {
                character?.let { newList ->
                    add(DataPoint(title = "Last know location",newList.location.name))
                    add(DataPoint(title = "Species",newList.species))
                    add(DataPoint(title = "Gender",newList.gender.displayName))
                    newList.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint(title = "Type",type))
                    }
                    add(DataPoint(title = "Origin",newList.origin.name))
                    add(DataPoint(title = "Episode count",newList.episodeUrls.size.toString()))
                }
            }
        }
    }

    LaunchedEffect (Unit){
        //delay(500)
         ktorClient
            .getCharacters(characterId)
            .onSuccess { apiCharacter ->
                character = apiCharacter
            }
            .onFailure {
                //handle exception
            }
    }

    LazyColumn (
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ){
        if (character == null){
            item { LoadingState() }
            return@LazyColumn
        }

        item{
            CharacterDetailsNamePlateComponent(
                name = character!!.name,
                status = character!!.status
            )
        }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }


        item { 
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                model = character!!.imageUrl,
                loading = {
                    LoadingState()
                },
                contentDescription = "Character Image")
        }
        
        items(characterDataPoints){
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