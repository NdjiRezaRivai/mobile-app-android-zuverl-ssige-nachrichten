package com.nachrichten.assessment

import com.nachrichten.assessment.domain.model.Article
import com.nachrichten.assessment.domain.model.Category
import com.nachrichten.assessment.domain.model.Source
import com.nachrichten.assessment.domain.repository.NewsRepository
import com.nachrichten.assessment.domain.usecase.GetCategoriesUseCase
import com.nachrichten.assessment.util.Resource
import kotlinx.coroutines.flow.Flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCategoriesUseCaseTest {

    private lateinit var getCategoriesUseCase: GetCategoriesUseCase
    private lateinit var fakeRepository: NewsRepository

    @Before
    fun setUp() {
        fakeRepository = object : NewsRepository {
            override fun getCategories(): List<Category> {
                return listOf(
                    Category("business", "Business", "Desc 1"),
                    Category("technology", "Technology", "Desc 2")
                )
            }

            override fun getSources(category: String, forceRefresh: Boolean): Flow<Resource<List<Source>>> = error("Not needed")
            override fun searchSources(category: String, query: String): Flow<Resource<List<Source>>> = error("Not needed")
            override fun getArticles(sourceId: String, page: Int, pageSize: Int, forceRefresh: Boolean): Flow<Resource<List<Article>>> = error("Not needed")
            override fun searchArticles(sourceId: String, query: String, page: Int, pageSize: Int): Flow<Resource<List<Article>>> = error("Not needed")
        }
        getCategoriesUseCase = GetCategoriesUseCase(fakeRepository)
    }

    @Test
    fun invoke_returnsCategoriesFromRepository() {
        val result = getCategoriesUseCase()
        assertEquals(2, result.size)
        assertEquals("business", result[0].id)
        assertEquals("technology", result[1].id)
    }
}
