package com.rickandmorty.rickandmortymultimodule.screen.search_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rickandmorty.network.KtorClient
import com.rickandmorty.rickandmortymultimodule.component.common.SimpleToolbar
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val ktorClient: KtorClient
) : ViewModel() {
    val searchTextFieldState = TextFieldState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchTextState = snapshotFlow { searchTextFieldState.text }
        .debounce(500) //Kullanıcı yazarken 500ms bekler. Eğer kullanıcı 500ms içinde yeni bir karakter yazarsa, önceki karakter iptal edilir ve yeniden başlar.
        .mapLatest { //gelen yeni veriyle önceki işlemi iptal edip en güncel veriyi işler.Burda da gelen  string değerine göre if condition yazıyoruz.
            if (it.isBlank()) "Await Your Command..." else it.toString()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //Eğer abone yoksa (UI bu değeri kullanmıyorsa), 5 saniye sonra durur.Abone bağlanırsa tekrar başlar.
            initialValue = "Await Your Command..."
        )
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
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
        
        val searchText by searchViewModel.searchTextState.collectAsStateWithLifecycle()
        
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            text = searchText,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
        )
    }
}

