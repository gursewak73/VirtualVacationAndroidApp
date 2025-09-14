package com.dream.virtualvacation.data.mapper

import com.dream.virtualvacation.domain.model.Video

// Placeholder DTO; replace with real API response later
data class VideoDto(
    val id: String,
    val title: String,
    val description: String
)

fun VideoDto.toDomain(): Video = Video(
    id = id,
    title = title,
    description = description
)



