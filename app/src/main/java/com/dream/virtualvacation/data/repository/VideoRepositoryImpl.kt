package com.dream.virtualvacation.data.repository

import com.dream.virtualvacation.data.mapper.VideoDto
import com.dream.virtualvacation.data.mapper.toDomain
import com.dream.virtualvacation.domain.model.Video
import com.dream.virtualvacation.domain.repository.VideoRepository

class VideoRepositoryImpl : VideoRepository {
    override suspend fun getVideoById(videoId: String): Video? {
        // Placeholder logic until real data source is added
        val mock = VideoDto(
            id = videoId,
            title = "Sample Video",
            description = "Sample description"
        )
        return mock.toDomain()
    }
}



