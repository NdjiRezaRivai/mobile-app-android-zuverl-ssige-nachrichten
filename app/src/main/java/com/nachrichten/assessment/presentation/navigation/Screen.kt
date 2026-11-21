package com.nachrichten.assessment.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Categories : Screen("categories")
    object Sources : Screen("sources/{category}") {
        fun createRoute(category: String) = "sources/$category"
    }
    object Articles : Screen("articles/{sourceId}/{sourceName}") {
        fun createRoute(sourceId: String, sourceName: String): String {
            val encodedName = URLEncoder.encode(sourceName, StandardCharsets.UTF_8.toString())
            return "articles/$sourceId/$encodedName"
        }
    }
    object ArticleDetail : Screen("article_detail?url={url}&title={title}") {
        fun createRoute(url: String, title: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "article_detail?url=$encodedUrl&title=$encodedTitle"
        }
    }
}
