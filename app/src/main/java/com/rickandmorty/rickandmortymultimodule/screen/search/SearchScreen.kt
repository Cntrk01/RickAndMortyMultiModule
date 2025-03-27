package com.rickandmorty.rickandmortymultimodule.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterListItem
import com.rickandmortymultimodule.common.ui_elements.DataPoint
import com.rickandmortymultimodule.common.ui_elements.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onCharacterClicked: (Int) -> Unit,
) {
    DisposableEffect(key1 = Unit){
        val job = searchViewModel.observeUserSearch()

        onDispose {
            job.cancel()
        }
    }

    Column (
        modifier = modifier,
    ){
        SimpleToolbar(title = "Search")

        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()

        AnimatedVisibility(visible = screenState is ScreenState.Searching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(5.dp)
                    .fillMaxWidth(),
                color = RickAction
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row (
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp), // burada padding ekledik.Backgroundun üzerine ekleyince küçücük kalıyor.Sonrasında eklememiz gerekiyor.
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = RickPrimary,
                )
                BasicTextField(
                    modifier = Modifier.weight(1f),
                    state = searchViewModel.searchTextFieldState,
                )
            }
            
            AnimatedVisibility(visible = searchViewModel.searchTextFieldState.text.isNotBlank()) {
                Icon(
                    modifier = Modifier.clickable {
                        searchViewModel.searchTextFieldState.edit {
                            delete(0,length)
                        }
                    },
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = RickAction,
                )
            }
        }

        when(val state = screenState){
            ScreenState.Empty -> {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    text = "Search for a character",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                )
            }
            ScreenState.Searching -> {}
            is ScreenState.Content -> SearchScreenContent(
                content = state,
                onStatusClicked = searchViewModel::toggleStatusFilter,
                onCharacterClicked = {onCharacterClicked(it)}
            )
            is ScreenState.Error -> {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    text = state.message,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                )
                
                Button(onClick = { searchViewModel.searchTextFieldState.clearText() }) {
                    Text(text = "Clear Query")
                }
            }
        }
    }
}

@Composable
private fun SearchScreenContent(
    content: ScreenState.Content,
    onStatusClicked: (CharacterStatus) -> Unit,
    onCharacterClicked: (Int) -> Unit
) {
    Text(
        text = "${content.results.size} results for '${content.userQuery}'",
        color = Color.White,
        modifier = Modifier.padding(
            start = 16.dp,
            bottom = 4.dp
        ),
        fontSize = 14.sp
    )

    Row (
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ){
        //Hepsi en başta seçili geliyor.Tıkladığımızı listeden çıkarıyor.
        content.filterState.statuses.forEach { status ->
            val isSelected = content.filterState.selectedStatuses.contains(status)
            val contentColor = if (isSelected) RickAction else Color.LightGray
            val count = content.results.filter { it.status == status }.size

            Row (
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = contentColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onStatusClicked(status)
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    modifier = Modifier
                        .background(color = contentColor)
                        .padding(4.dp),
                    text = count.toString(),
                    color = RickPrimary,
                )
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    text = status.displayName,
                    color = Color.White,
                )
            }
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier.clipToBounds(), // alive dead unknowna tıkladığımızda görünüm bozuluyor.Yani içerdeki itemler anlık yukarı taşıp tekrar görünüyor bundan dolayı lazycolumn sınırları dışına çıkmamasını sağladım...
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
        ) {
            val filteredItem = content.results.filter { content.filterState.selectedStatuses.contains(it.status) }

            items(
                items = filteredItem,
                key = { character ->
                    character.id
                }
            ) { character ->
                val dataPoints = buildList {
                    add(
                        DataPoint(
                            "Last known location",
                            character.location.name
                        )
                    )
                    add(DataPoint("Species", character.species))
                    add(
                        DataPoint(
                            "Gender",
                            character.gender.displayName
                        )
                    )
                    character.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(
                        DataPoint(
                            "Episode count",
                            character.episodeIds.size.toString()
                        )
                    )
                }

                CharacterListItem(
                    character = character,
                    characterDataPoints = dataPoints,
                    onClick = { onCharacterClicked(character.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(RickPrimary, Color.Transparent))
                )
        )
    }
}

