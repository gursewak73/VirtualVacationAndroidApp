package com.dream.virtualvacation.domain.repository

import com.dream.virtualvacation.domain.model.Video

interface VideoRepository {
    suspend fun getVideoById(videoId: String): Video?
}



