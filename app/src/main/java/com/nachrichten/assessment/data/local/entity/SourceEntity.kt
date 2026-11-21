package com.nachrichten.assessment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nachrichten.assessment.domain.model.Source

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String
)

fun SourceEntity.toSource(): Source {
    return Source(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country
    )
}
