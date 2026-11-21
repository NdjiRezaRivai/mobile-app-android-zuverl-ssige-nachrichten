package com.nachrichten.assessment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nachrichten.assessment.domain.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

fun ArticleEntity.toArticle(): Article {
    return Article(
        url = url,
        sourceId = sourceId,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}
