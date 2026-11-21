package com.nachrichten.assessment.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.nachrichten.assessment.data.local.entity.ArticleEntity
import com.nachrichten.assessment.domain.model.Article

data class ArticleSourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)

data class ArticleDto(
    @SerializedName("source") val source: ArticleSourceDto?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("content") val content: String?
)

fun ArticleDto.toArticleEntity(targetSourceId: String = ""): ArticleEntity {
    return ArticleEntity(
        url = url,
        sourceId = source?.id ?: targetSourceId,
        sourceName = source?.name ?: targetSourceId,
        author = author ?: "Unknown Author",
        title = title ?: "No Title",
        description = description ?: "",
        urlToImage = urlToImage,
        publishedAt = publishedAt ?: "",
        content = content ?: ""
    )
}

fun ArticleDto.toArticle(): Article {
    return Article(
        url = url,
        sourceId = source?.id ?: "",
        sourceName = source?.name ?: "Unknown",
        author = author ?: "Unknown Author",
        title = title ?: "No Title",
        description = description ?: "",
        urlToImage = urlToImage,
        publishedAt = publishedAt ?: "",
        content = content ?: ""
    )
}
