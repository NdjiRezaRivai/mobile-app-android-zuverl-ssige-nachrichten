package com.nachrichten.assessment.domain.usecase

import com.nachrichten.assessment.domain.model.Source
import com.nachrichten.assessment.domain.repository.NewsRepository
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.flow.Flow

class GetSourcesUseCase(
    private val repository: NewsRepository
) {
    operator fun invoke(category: String, forceRefresh: Boolean = false): Flow<Resource<List<Source>>> {
        return repository.getSources(category, forceRefresh)
    }
}
