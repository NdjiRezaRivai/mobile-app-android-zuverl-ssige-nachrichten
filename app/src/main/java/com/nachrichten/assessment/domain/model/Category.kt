package com.nachrichten.assessment.domain.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String = ""
)
