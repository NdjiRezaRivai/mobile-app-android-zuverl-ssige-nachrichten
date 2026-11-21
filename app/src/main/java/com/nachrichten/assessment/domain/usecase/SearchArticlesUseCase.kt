package com.nachrichten.assessment.domain.usecase

import com.nachrichten.assessment.domain.model.Article
import com.nachrichten.assessment.domain.repository.NewsRepository
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.flow.Flow

class SearchArticlesUseCase(
    private val repository: NewsRepository
) {
    operator fun invoke(
        sourceId: String,
        query: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Resource<List<Article>>> {
        return repository.searchArticles(sourceId, query, page, pageSize)
    }
}
