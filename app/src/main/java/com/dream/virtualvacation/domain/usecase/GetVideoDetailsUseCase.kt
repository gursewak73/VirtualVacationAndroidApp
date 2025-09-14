package com.dream.virtualvacation.domain.usecase

import com.dream.virtualvacation.domain.model.Video
import com.dream.virtualvacation.domain.repository.VideoRepository

class GetVideoDetailsUseCase(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): Video? = repository.getVideoById(videoId)
}



