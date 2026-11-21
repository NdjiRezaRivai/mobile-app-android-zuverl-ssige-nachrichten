package com.nachrichten.assessment.domain.repository

import com.nachrichten.assessment.domain.model.Article
import com.nachrichten.assessment.domain.model.Category
import com.nachrichten.assessment.domain.model.Source
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getCategories(): List<Category>
    fun getSources(category: String, forceRefresh: Boolean = false): Flow<Resource<List<Source>>>
    fun searchSources(category: String, query: String): Flow<Resource<List<Source>>>
    fun getArticles(sourceId: String, page: Int, pageSize: Int, forceRefresh: Boolean = false): Flow<Resource<List<Article>>>
    fun searchArticles(sourceId: String, query: String, page: Int, pageSize: Int): Flow<Resource<List<Article>>>
}
