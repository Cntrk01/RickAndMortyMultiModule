package com.rickandmorty.rickandmortymultimodule.screen.all_episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickandmortymultimodule.domain.repository.AllEpisodesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllEpisodesViewModel @Inject constructor(
    private val episodeRepository: AllEpisodesRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow<AllEpisodesUiState>(AllEpisodesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun refreshAllEpisodes(forceRefresh: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (forceRefresh) _uiState.update { AllEpisodesUiState.Loading }

        episodeRepository.getAllEpisodes()
            .onSuccess { episodeList ->
                //bunu update içerisinde yapmıştım ama 3.sezon en son sıraya düşüyordu garip bir şekilde burda yaptım düzeldi.Sanırım sıralamada karışıyordu onuda sorted ve groupby ile düzelttim.
                val groupedEpisodes = episodeList
                    .sortedBy { it.seasonNumber } // İlk olarak sıralama yap
                    .groupBy { it.seasonNumber.toString() } // Sonra gruplama yap

                _uiState.update {
                    AllEpisodesUiState.Success(
                        data = groupedEpisodes.mapKeys {
                            "Season ${it.key}"
                        }
                    )
                }
            }.onFailure {
                _uiState.update { AllEpisodesUiState.Error }
            }
    }
}