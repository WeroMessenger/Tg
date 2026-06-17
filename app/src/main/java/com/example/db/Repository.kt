package com.example.db

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameProgressDao: GameProgressDao) {
    val progress: Flow<GameProgress?> = gameProgressDao.getProgress()

    suspend fun saveProgress(progress: GameProgress) {
        gameProgressDao.saveProgress(progress)
    }

    suspend fun clearProgress() {
        gameProgressDao.deleteProgress()
    }
}
