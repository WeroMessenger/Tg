package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val id: Int = 1,
    val difficulty: String = "MEDIUM",
    val experience: Int = 0,
    val level: Int = 1,
    val currentChapter: Int = 1,
    val unlockedLocations: String = "Завод,Метро,Парк,Жилой Квартал", // comma-separated names of unlocked locations
    val discoveredClues: String = "anna_profile,dmitry_profile,marina_profile", // starts with the three initial profiles
    val analyzedClues: String = "", // clues that have been deeply analyzed
    val connectedClues: String = "", // pairs of "id1->id2", separated by comma
    val unlockedAchievements: String = "", // comma-separated achievement IDs
    val chatHistory: String = "", // chat messages state
    val bossWarningShown: Boolean = false,
    val isGameInProgress: Boolean = false,
    val endingType: String = "" // "GOOD", "MEDIUM", "BAD" or empty
)
