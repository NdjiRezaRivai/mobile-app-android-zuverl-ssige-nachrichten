package com.nachrichten.assessment.presentation.sources

import com.nachrichten.assessment.domain.model.Source

data class SourcesState(
    val category: String = "",
    val sources: List<Source> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
