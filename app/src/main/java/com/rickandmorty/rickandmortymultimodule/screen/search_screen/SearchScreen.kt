package com.rickandmorty.rickandmortymultimodule.screen.search_screen

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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.CharacterStatus
import com.rickandmorty.rickandmortymultimodule.component.character.CharacterListItem
import com.rickandmorty.rickandmortymultimodule.component.common.DataPoint
import com.rickandmorty.rickandmortymultimodule.component.common.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.repositories.SearchRepository
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface  SearchState{
    data object Empty : SearchState
    data class UserQuery(val query : String) : SearchState
}

sealed interface ScreenState{
    data object Empty : ScreenState
    data object Searching : ScreenState
    data class Error(val message : String) : ScreenState
    data class Content(
        val userQuery : String,
        val results : List<Character>
    ) : ScreenState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    val searchTextFieldState = TextFieldState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchTextState : StateFlow<SearchState> = snapshotFlow { searchTextFieldState.text }
        .debounce(500) //Kullanıcı yazarken 500ms bekler. Eğer kullanıcı 500ms içinde yeni bir karakter yazarsa, önceki karakter iptal edilir ve yeniden başlar.
        .mapLatest { //gelen yeni veriyle önceki işlemi iptal edip en güncel veriyi işler.Burda da gelen  string değerine göre if condition yazıyoruz.
            if (it.isBlank()) SearchState.Empty else SearchState.UserQuery(it.toString())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //Eğer abone yoksa (UI bu değeri kullanmıyorsa), 5 saniye sonra durur.Abone bağlanırsa tekrar başlar.
            initialValue = SearchState.Empty,
        )

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState   = _uiState .asStateFlow()

    fun observeUserSearch() = viewModelScope.launch (Dispatchers.IO){
        searchTextState.collectLatest{ searchState->
            when(searchState){
                SearchState.Empty -> {
                    _uiState.update {
                        ScreenState.Empty
                    }
                }
                is SearchState.UserQuery -> searchAllCharacters(query = searchState.query)
            }
        }
    }

    private fun searchAllCharacters(
        query: String
    ) = viewModelScope.launch (Dispatchers.IO){
        _uiState.update { ScreenState.Searching }

        searchRepository.getAllCharactersByName(searchQuery = query)
            .onSuccess { characters->
                _uiState.update {
                    ScreenState.Content(
                        userQuery = query,
                        results = characters
                    )
                }
            }
            .onFailure { exception ->
                _uiState.update {ScreenState.Error("No Search Results Found")}
            }
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
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

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
        
        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()

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
                onStatusClicked = {},
                onCharacterClicked = {}
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
        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
        fontSize = 14.sp
    )

    Box {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
            modifier = Modifier.clipToBounds()
        ) {
            items(
                content.results
            ) { character ->
                val dataPoints = buildList {
                    add(DataPoint("Last known location", character.location.name))
                    add(DataPoint("Species", character.species))
                    add(DataPoint("Gender", character.gender.displayName))
                    character.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(DataPoint("Episode count", character.episodeIds.size.toString()))
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

