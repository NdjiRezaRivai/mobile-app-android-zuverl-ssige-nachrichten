package com.nachrichten.assessment.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.nachrichten.assessment.data.local.entity.SourceEntity
import com.nachrichten.assessment.domain.model.Source

data class SourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("country") val country: String?
)

fun SourceDto.toSourceEntity(categoryParam: String = ""): SourceEntity {
    return SourceEntity(
        id = id ?: name ?: "",
        name = name ?: "Unknown Source",
        description = description ?: "",
        url = url ?: "",
        category = category ?: categoryParam,
        language = language ?: "en",
        country = country ?: "us"
    )
}

fun SourceDto.toSource(): Source {
    return Source(
        id = id ?: name ?: "",
        name = name ?: "Unknown Source",
        description = description ?: "",
        url = url ?: "",
        category = category ?: "",
        language = language ?: "en",
        country = country ?: "us"
    )
}
