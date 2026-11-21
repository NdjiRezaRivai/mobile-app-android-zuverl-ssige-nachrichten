package com.nachrichten.assessment.domain.usecase

import com.nachrichten.assessment.domain.model.Category
import com.nachrichten.assessment.domain.repository.NewsRepository

class GetCategoriesUseCase(
    private val repository: NewsRepository
) {
    operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}
