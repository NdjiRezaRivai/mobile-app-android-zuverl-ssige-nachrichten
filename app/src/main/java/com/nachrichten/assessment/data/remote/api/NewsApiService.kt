package com.nachrichten.assessment.data.remote.api

import com.nachrichten.assessment.data.remote.dto.ArticlesResponseDto
import com.nachrichten.assessment.data.remote.dto.SourcesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines/sources")
    suspend fun getSources(
        @Query("category") category: String? = null,
        @Query("apiKey") apiKey: String
    ): SourcesResponseDto

    @GET("everything")
    suspend fun getArticlesBySource(
        @Query("sources") sources: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): ArticlesResponseDto

    @GET("everything")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("sources") sources: String? = null,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): ArticlesResponseDto

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("sources") sources: String? = null,
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): ArticlesResponseDto
}
