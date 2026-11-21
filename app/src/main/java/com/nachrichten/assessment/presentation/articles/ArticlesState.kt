package com.nachrichten.assessment.presentation.articles

import com.nachrichten.assessment.domain.model.Article

data class ArticlesState(
    val sourceId: String = "",
    val sourceName: String = "",
    val articles: List<Article> = emptyList(),
    val searchQuery: String = "",
    val page: Int = 1,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endOfPaginationReached: Boolean = false,
    val errorMessage: String? = null
)
