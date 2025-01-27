package org.michaelbel.movies.repository

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.persistence.database.entity.ImageDb

interface ImageRepository {

    fun imagesFlow(movieId: Int): Flow<List<ImageDb>>

    suspend fun images(movieId: Int)
}