package com.nachrichten.assessment.presentation.categories

import com.nachrichten.assessment.domain.model.Category

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)
