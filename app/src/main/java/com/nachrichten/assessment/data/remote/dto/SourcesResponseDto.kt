package com.nachrichten.assessment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SourcesResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("sources") val sources: List<SourceDto>?
)
