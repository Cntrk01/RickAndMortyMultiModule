package com.rickandmorty.rickandmortymultimodule.screen.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmortymultimodule.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
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

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    val searchTextFieldState = TextFieldState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchTextState : StateFlow<SearchState> = snapshotFlow { searchTextFieldState.text }
        .debounce(500) //Kullanıcı yazarken 500ms bekler. Eğer kullanıcı 500ms içinde yeni bir karakter yazarsa, önceki karakter iptal edilir ve yeniden başlar.
        .mapLatest { //gelen yeni veriyle önceki işlemi iptal edip en güncel veriyi işler.Burda da gelen  string değerine göre if condition yazıyoruz.
            if (it.isBlank())
                SearchState.Empty
            else
                SearchState.UserQuery(it.toString())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //Eğer abone yoksa (UI bu değeri kullanmıyorsa), 5 saniye sonra durur.Abone bağlanırsa tekrar başlar.
            initialValue = SearchState.Empty,
        )
    //Burda initial valueye eklemem birşey ifade etmiyor 1 kere oluşturulduğu için yanlızca 1 kez çalışıcak yani ekran geçişlerinde tekrar oluşturulmuyor.Bundan dolayı initial value empty vermem search durumundaki state sorununu ortaya çıkarmıyor.
    // if (searchTextFieldState.text.isBlank()) SearchState.Empty
    // else SearchState.UserQuery(searchTextFieldState.text.toString()),

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState = _uiState .asStateFlow()

    fun observeUserSearch() = viewModelScope.launch (Dispatchers.IO){
        searchTextState.collectLatest{ searchState->
            when(searchState){
                SearchState.Empty -> {
                    _uiState.update {
                        ScreenState.Empty
                    }
                }
                is SearchState.UserQuery -> {
                    val currentState = _uiState.value
                    //Burda bu kontrolü ekleme sebebim her detay sayfasına gidip döndüğümde servise tekrar istek atıp searchAllCharacters() methodunu çağırıyordu.
                    //Bende hali hazırda bir state olduğu için bunun kontrolünü sağladım.Böylelikle detaydan dönünce state korumasına devam ediyor.
                    if (currentState is ScreenState.Content && currentState.userQuery == searchState.query) {
                        return@collectLatest
                    }else{
                        searchAllCharacters(query = searchState.query)
                    }
                }
            }
        }
    }

    fun toggleStatusFilter(status: CharacterStatus) {
        _uiState.update {
            val currentState = (it as? ScreenState.Content) ?: return@update it
            val currentSelectedStatuses = currentState.filterState.selectedStatuses

            val newStatus = if (currentSelectedStatuses.contains(status)){
                currentSelectedStatuses - status
            }else{
                currentSelectedStatuses + status
            }

            return@update currentState.copy(
                filterState = currentState.filterState.copy(
                    selectedStatuses = newStatus
                )
            )
        }
    }

    private fun searchAllCharacters(query: String)= viewModelScope.launch (Dispatchers.IO){
        _uiState.update { ScreenState.Searching }
        delay(1200) //LinearProgressIndicator için.
        searchRepository
            .getAllCharactersByName(searchQuery = query)
            .onSuccess { characters->
                val allStatus = characters.map { it.status }.toSet().toList().sortedBy { it.displayName }

                _uiState.update {
                    ScreenState.Content(
                        userQuery = query,
                        results = characters,
                        filterState = ScreenState.Content.FilterState(
                            statuses = allStatus,
                            selectedStatuses = allStatus,
                        )
                    )
                }
            }
            .onFailure { exception ->
                _uiState.update {ScreenState.Error("No Search Results Found")}
            }
    }
}