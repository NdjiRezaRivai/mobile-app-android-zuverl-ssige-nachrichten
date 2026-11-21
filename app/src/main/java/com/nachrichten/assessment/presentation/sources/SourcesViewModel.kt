package com.nachrichten.assessment.presentation.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nachrichten.assessment.domain.usecase.GetSourcesUseCase
import com.nachrichten.assessment.domain.usecase.SearchSourcesUseCase
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SourcesViewModel(
    private val category: String,
    private val getSourcesUseCase: GetSourcesUseCase,
    private val searchSourcesUseCase: SearchSourcesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SourcesState(category = category))
    val uiState: StateFlow<SourcesState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSources(forceRefresh = false)
    }

    fun loadSources(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getSourcesUseCase(category, forceRefresh).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                sources = result.data ?: emptyList(),
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                sources = result.data ?: it.sources,
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (newQuery.isBlank()) {
                loadSources(forceRefresh = false)
            } else {
                searchSourcesUseCase(category, newQuery).collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> _uiState.update {
                            it.copy(sources = result.data ?: emptyList(), isLoading = false, errorMessage = null)
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(sources = result.data ?: emptyList(), isLoading = false, errorMessage = result.message)
                        }
                    }
                }
            }
        }
    }

    class Factory(
        private val category: String,
        private val getSourcesUseCase: GetSourcesUseCase,
        private val searchSourcesUseCase: SearchSourcesUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SourcesViewModel(category, getSourcesUseCase, searchSourcesUseCase) as T
        }
    }
}
