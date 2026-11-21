package com.nachrichten.assessment.presentation.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nachrichten.assessment.domain.model.Article
import com.nachrichten.assessment.domain.usecase.GetArticlesUseCase
import com.nachrichten.assessment.domain.usecase.SearchArticlesUseCase
import com.nachrichten.assessment.util.Constants
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val sourceId: String,
    private val sourceName: String,
    private val getArticlesUseCase: GetArticlesUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ArticlesState(sourceId = sourceId, sourceName = sourceName)
    )
    val uiState: StateFlow<ArticlesState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadArticles(page = 1, forceRefresh = false)
    }

    fun loadArticles(page: Int = 1, forceRefresh: Boolean = false) {
        if (page > 1 && (_uiState.value.isLoadingMore || _uiState.value.endOfPaginationReached)) return

        viewModelScope.launch {
            val query = _uiState.value.searchQuery
            val flow = if (query.isBlank()) {
                getArticlesUseCase(sourceId, page, Constants.PAGE_SIZE, forceRefresh)
            } else {
                searchArticlesUseCase(sourceId, query, page, Constants.PAGE_SIZE)
            }

            flow.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            if (page == 1) it.copy(isLoading = true, errorMessage = null)
                            else it.copy(isLoadingMore = true, errorMessage = null)
                        }
                    }
                    is Resource.Success -> {
                        val newArticles = result.data ?: emptyList()
                        _uiState.update { currentState ->
                            val combined = if (page == 1) newArticles else (currentState.articles + newArticles).distinctBy { it.url }
                            currentState.copy(
                                articles = combined,
                                page = page,
                                isLoading = false,
                                isLoadingMore = false,
                                endOfPaginationReached = newArticles.size < Constants.PAGE_SIZE,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        val fallback = result.data ?: _uiState.value.articles
                        _uiState.update { currentState ->
                            currentState.copy(
                                articles = if (page == 1) fallback else currentState.articles,
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (!_uiState.value.isLoading && !_uiState.value.isLoadingMore && !_uiState.value.endOfPaginationReached) {
            loadArticles(page = _uiState.value.page + 1, forceRefresh = false)
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery, page = 1, endOfPaginationReached = false) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadArticles(page = 1, forceRefresh = false)
        }
    }

    class Factory(
        private val sourceId: String,
        private val sourceName: String,
        private val getArticlesUseCase: GetArticlesUseCase,
        private val searchArticlesUseCase: SearchArticlesUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticlesViewModel(sourceId, sourceName, getArticlesUseCase, searchArticlesUseCase) as T
        }
    }
}
