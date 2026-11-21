package com.nachrichten.assessment.data.repository

import com.nachrichten.assessment.data.local.dao.ArticleDao
import com.nachrichten.assessment.data.local.dao.SourceDao
import com.nachrichten.assessment.data.local.entity.toArticle
import com.nachrichten.assessment.data.local.entity.toSource
import com.nachrichten.assessment.data.remote.api.NewsApiService
import com.nachrichten.assessment.data.remote.dto.toArticle
import com.nachrichten.assessment.data.remote.dto.toArticleEntity
import com.nachrichten.assessment.data.remote.dto.toSource
import com.nachrichten.assessment.data.remote.dto.toSourceEntity
import com.nachrichten.assessment.domain.model.Article
import com.nachrichten.assessment.domain.model.Category
import com.nachrichten.assessment.domain.model.Source
import com.nachrichten.assessment.domain.repository.NewsRepository
import com.nachrichten.assessment.util.Constants
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(
    private val apiService: NewsApiService,
    private val sourceDao: SourceDao,
    private val articleDao: ArticleDao
) : NewsRepository {

    override fun getCategories(): List<Category> {
        return listOf(
            Category("business", "Business", "Business, finance, and stock markets news", "business"),
            Category("entertainment", "Entertainment", "Movies, music, celebrities, and culture", "movie"),
            Category("general", "General", "Global news, breaking stories, and updates", "public"),
            Category("health", "Health", "Medicine, wellness, fitness, and healthcare", "health"),
            Category("science", "Science", "Space, nature, research, and discoveries", "science"),
            Category("sports", "Sports", "Football, basketball, racing, and sports news", "sports"),
            Category("technology", "Technology", "Gadgets, software, AI, and innovations", "computer")
        )
    }

    override fun getSources(category: String, forceRefresh: Boolean): Flow<Resource<List<Source>>> = flow {
        emit(Resource.Loading())

        val localEntities = sourceDao.getSourcesByCategory(category).first()
        val localSources = localEntities.map { it.toSource() }

        if (localSources.isNotEmpty() && !forceRefresh) {
            emit(Resource.Success(localSources))
        }

        try {
            val response = apiService.getSources(
                category = category,
                apiKey = Constants.API_KEY
            )

            if (response.status == "ok" && response.sources != null) {
                val remoteEntities = response.sources.map { it.toSourceEntity(category) }
                sourceDao.clearSourcesByCategory(category)
                sourceDao.insertSources(remoteEntities)

                val updatedSources = remoteEntities.map { it.toSource() }
                emit(Resource.Success(updatedSources))
            } else {
                emit(Resource.Error("Failed to load news sources", localSources))
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "Connection error. Displaying offline data."
            if (localSources.isNotEmpty()) {
                emit(Resource.Error(errorMessage, localSources))
            } else {
                emit(Resource.Error(errorMessage, emptyList()))
            }
        }
    }

    override fun searchSources(category: String, query: String): Flow<Resource<List<Source>>> = flow {
        emit(Resource.Loading())
        val localEntities = sourceDao.searchSources(category, query).first()
        val localSources = localEntities.map { it.toSource() }
        emit(Resource.Success(localSources))
    }

    override fun getArticles(
        sourceId: String,
        page: Int,
        pageSize: Int,
        forceRefresh: Boolean
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        val localEntities = articleDao.getArticlesBySource(sourceId).first()
        val localArticles = localEntities.map { it.toArticle() }

        if (page == 1 && localArticles.isNotEmpty() && !forceRefresh) {
            emit(Resource.Success(localArticles))
        }

        try {
            val response = apiService.getArticlesBySource(
                sources = sourceId,
                page = page,
                pageSize = pageSize,
                apiKey = Constants.API_KEY
            )

            if (response.status == "ok" && response.articles != null) {
                val remoteEntities = response.articles
                    .filter { !it.title.isNull_or_blank_or_removed() }
                    .map { it.toArticleEntity(sourceId) }

                if (page == 1) {
                    articleDao.clearArticlesBySource(sourceId)
                }
                articleDao.insertArticles(remoteEntities)

                val remoteArticles = response.articles
                    .filter { !it.title.isNull_or_blank_or_removed() }
                    .map { it.toArticle() }

                emit(Resource.Success(remoteArticles))
            } else {
                emit(Resource.Error("Failed to fetch news articles", if (page == 1) localArticles else null))
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "Network unavailable. Showing cached articles."
            if (page == 1 && localArticles.isNotEmpty()) {
                emit(Resource.Error(errorMessage, localArticles))
            } else {
                emit(Resource.Error(errorMessage, null))
            }
        }
    }

    override fun searchArticles(
        sourceId: String,
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchArticles(
                query = query,
                sources = sourceId.ifEmpty { null },
                page = page,
                pageSize = pageSize,
                apiKey = Constants.API_KEY
            )

            if (response.status == "ok" && response.articles != null) {
                val articles = response.articles
                    .filter { !it.title.isNull_or_blank_or_removed() }
                    .map { it.toArticle() }
                emit(Resource.Success(articles))
            } else {
                val localEntities = articleDao.searchArticlesBySource(sourceId, query).first()
                emit(Resource.Error("Search failed", localEntities.map { it.toArticle() }))
            }
        } catch (e: Exception) {
            val localEntities = articleDao.searchArticlesBySource(sourceId, query).first()
            val errorMessage = e.localizedMessage ?: "Offline mode. Searched local database."
            emit(Resource.Error(errorMessage, localEntities.map { it.toArticle() }))
        }
    }

    private fun String?.isNull_or_blank_or_removed(): Boolean {
        return this.isNullOrBlank() || this.contains("[Removed]", ignoreCase = true)
    }
}
