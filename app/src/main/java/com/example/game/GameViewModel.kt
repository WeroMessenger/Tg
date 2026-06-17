package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.AppDatabase
import com.example.db.GameProgress
import com.example.db.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val _progressState = MutableStateFlow<GameProgress?>(null)
    val progressState: StateFlow<GameProgress?> = _progressState.asStateFlow()

    // Transient UI states
    private val _selectedApp = MutableStateFlow<String?>(null) // e.g. "dossier", "database", "map", etc.
    val selectedApp: StateFlow<String?> = _selectedApp.asStateFlow()

    private val _zoomClueId = MutableStateFlow<String?>(null) // currently zoomed clue
    val zoomClueId: StateFlow<String?> = _zoomClueId.asStateFlow()

    // Investigation Board transient links
    private val _firstSelectedBoardClue = MutableStateFlow<String?>(null)
    val firstSelectedBoardClue: StateFlow<String?> = _firstSelectedBoardClue.asStateFlow()

    // Database search filter
    private val _dbSearchQuery = MutableStateFlow("")
    val dbSearchQuery: StateFlow<String> = _dbSearchQuery.asStateFlow()

    // Interactive HEX decryptor
    private val _hexInput = MutableStateFlow("47686F73745F3133206973204B757A6D696E")
    val hexInput: StateFlow<String> = _hexInput.asStateFlow()
    private val _hexDecryptedText = MutableStateFlow("")
    val hexDecryptedText: StateFlow<String> = _hexDecryptedText.asStateFlow()

    // Notification toast or status
    private val _gameMessage = MutableStateFlow<String?>(null)
    val gameMessage: StateFlow<String?> = _gameMessage.asStateFlow()

    init {
        val progressDao = AppDatabase.getDatabase(application).gameProgressDao()
        repository = GameRepository(progressDao)

        viewModelScope.launch(Dispatchers.IO) {
            repository.progress.collectLatest { progress ->
                _progressState.value = progress
            }
        }
    }

    fun selectApp(appName: String?) {
        _selectedApp.value = appName
    }

    fun zoomClue(clueId: String?) {
        _zoomClueId.value = clueId
        if (clueId != null) {
            markClueAnalyzed(clueId)
        }
    }

    fun setDbSearchQuery(query: String) {
        _dbSearchQuery.value = query
    }

    fun setHexInput(input: String) {
        _hexInput.value = input
        try {
            val cleanHex = input.replace("0x", "").replace(" ", "").trim()
            val text = cleanHex.chunked(2)
                .map { it.toInt(16).toChar() }
                .joinToString("")
            _hexDecryptedText.value = text
        } catch (e: Exception) {
            _hexDecryptedText.value = "Ошибка декодирования"
        }
    }

    fun clearGameMessage() {
        _gameMessage.value = null
    }

    fun showMessage(text: String) {
        _gameMessage.value = text
    }

    // New Game setup
    fun startNewGame(difficulty: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProgress = GameProgress(
                difficulty = difficulty,
                experience = 0,
                level = 1,
                currentChapter = 1,
                unlockedLocations = "home", // starts at Police Office only
                discoveredClues = "anna_profile,dmitry_profile,marina_profile",
                analyzedClues = "",
                connectedClues = "",
                unlockedAchievements = "",
                chatHistory = "",
                bossWarningShown = false,
                isGameInProgress = true,
                endingType = ""
            )
            repository.saveProgress(newProgress)
            showMessage("Игра началась! Сложность: ${getDifficultyLabel(difficulty)}")
        }
    }

    fun selectOption(chatId: String, sender: String, optionText: String, xpReward: Int, nextId: String, triggersClue: String) {
        val current = _progressState.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val updatedXp = current.experience + xpReward
            val finalXp = updatedXp % 100
            val levelIncrease = updatedXp / 100
            val updatedLevel = current.level + levelIncrease

            var updatedClues = current.discoveredClues
            if (triggersClue.isNotEmpty() && !current.discoveredClues.split(",").contains(triggersClue)) {
                updatedClues = if (updatedClues.isEmpty()) triggersClue else "$updatedClues,$triggersClue"
            }

            var updatedHistory = current.chatHistory
            val chatStateKey = "$chatId:$nextId"
            updatedHistory = if (updatedHistory.isEmpty()) chatStateKey else "$updatedHistory,$chatStateKey"

            val updatedProgress = current.copy(
                experience = finalXp,
                level = updatedLevel,
                discoveredClues = updatedClues,
                chatHistory = updatedHistory
            )

            // Trigger Achievement for first option/clue if applicable
            var finalProgress = updatedProgress
            if (levelIncrease > 0) {
                finalProgress = unlockAchievementDirectly(finalProgress, "analyst")
            }

            repository.saveProgress(finalProgress)
            showMessage("Получено +$xpReward XP!")
        }
    }

    private fun getDifficultyLabel(diff: String): String {
        return when (diff) {
            "EASY" -> "Легкий (Подсказки)"
            "MEDIUM" -> "Средний (Норма)"
            "HARD" -> "Сложный (OSINT профи)"
            else -> "Норма"
        }
    }

    // Travel to Location
    fun travelToLocation(locId: String) {
        val current = _progressState.value ?: return
        val loc = StaticGameData.MAP_LOCATIONS.firstOrNull { it.id == locId } ?: return

        viewModelScope.launch(Dispatchers.IO) {
            var updatedClues = current.discoveredClues.split(",").toMutableSet()
            // Add found clues
            loc.cluesFound.forEach { clueId ->
                updatedClues.add(clueId)
            }

            val newlyFoundSize = updatedClues.size - current.discoveredClues.split(",").filter { it.isNotEmpty() }.size
            val xpGained = if (newlyFoundSize > 0) newlyFoundSize * 35 else 10

            val totalXp = current.experience + xpGained
            val finalXp = totalXp % 100
            val addedLevels = totalXp / 100
            val currentLvl = current.level + addedLevels

            var progress = current.copy(
                discoveredClues = updatedClues.joinToString(","),
                experience = finalXp,
                level = currentLvl
            )

            if (locId == "warehouse") {
                progress = unlockAchievementDirectly(progress, "sleuth")
            }

            repository.saveProgress(progress)
            showMessage("Вы прибыли в район: ${loc.name}. Получено +$xpGained XP!")
        }
    }

    // Try linking two clues on the investigation board
    fun selectBoardClue(clueId: String) {
        val first = _firstSelectedBoardClue.value
        if (first == null) {
            _firstSelectedBoardClue.value = clueId
        } else if (first == clueId) {
            _firstSelectedBoardClue.value = null // unselect
        } else {
            // Attempt to link first -> clueId or clueId -> first
            attemptLinkClues(first, clueId)
            _firstSelectedBoardClue.value = null
        }
    }

    private fun attemptLinkClues(clue1: String, clue2: String) {
        val current = _progressState.value ?: return

        // Verify valid connections:
        // C1: anna_profile <-> anna_note
        // C2: anna_note <-> factory_photo
        // C3: factory_photo <-> factory_coords
        // C4: dmitry_profile <-> taxi_trip
        // C5: taxi_trip <-> gas_receipt
        // C6: gas_receipt <-> camera_station
        // C7: marina_profile <-> encrypted_email
        // C8: encrypted_email <-> browser_history
        // C9: browser_history <-> subway_log

        val parsed1 = if (clue1 < clue2) clue1 else clue2
        val parsed2 = if (clue1 < clue2) clue2 else clue1
        val connectionKey = "$parsed1->$parsed2"

        val isValid = when {
            // Lebedeva
            (parsed1 == "anna_note" && parsed2 == "anna_profile") -> true
            (parsed1 == "anna_note" && parsed2 == "factory_photo") -> {
                // can only link if unlocked
                true
            }
            (parsed1 == "factory_coords" && parsed2 == "factory_photo") -> true

            // Krylov
            (parsed1 == "dmitry_profile" && parsed2 == "taxi_trip") -> true
            (parsed1 == "gas_receipt" && parsed2 == "taxi_trip") -> true
            (parsed1 == "camera_station" && parsed2 == "gas_receipt") -> true

            // Sokolova
            (parsed1 == "encrypted_email" && parsed2 == "marina_profile") -> true
            (parsed1 == "browser_history" && parsed2 == "encrypted_email") -> true
            (parsed1 == "browser_history" && parsed2 == "subway_log") -> true

            else -> false
        }

        if (!isValid) {
            showMessage("Связь не подтверждена. Эти улики логически не связаны.")
            return
        }

        // Connection is valid!
        viewModelScope.launch(Dispatchers.IO) {
            val connectionsSet = current.connectedClues.split(",").filter { it.isNotEmpty() }.toMutableSet()
            if (connectionsSet.contains(connectionKey)) {
                showMessage("Связь уже установлена.")
                return@launch
            }

            connectionsSet.add(connectionKey)

            // Grant XP
            val totalXp = current.experience + 40
            val finalXp = totalXp % 100
            val addedLevels = totalXp / 100
            val currentLvl = current.level + addedLevels

            var updatedCluesSet = current.discoveredClues.split(",").filter { it.isNotEmpty() }.toMutableSet()
            var unlockedMapLocations = current.unlockedLocations.split(",").filter { it.isNotEmpty() }.toMutableSet()

            // Handle unlock consequences of connections
            if (connectionKey == "anna_note->factory_photo") {
                updatedCluesSet.add("factory_coords")
                showMessage("Анализ выявил координаты скрытого объекта!")
            }
            if (connectionKey == "factory_coords->factory_photo") {
                unlockedMapLocations.add("factory")
                showMessage("Локация 'Завод Красный Октябрь' разблокирована на карте!")
            }
            if (connectionKey == "dmitry_profile->taxi_trip") {
                updatedCluesSet.add("gas_receipt")
            }
            if (connectionKey == "gas_receipt->taxi_trip") {
                updatedCluesSet.add("camera_station")
            }
            if (connectionKey == "camera_station->gas_receipt") {
                unlockedMapLocations.add("railway")
                showMessage("Локация 'Железнодорожная Станция' разблокирована на карте!")
            }
            if (connectionKey == "encrypted_email->marina_profile") {
                updatedCluesSet.add("browser_history")
            }
            if (connectionKey == "browser_history->encrypted_email") {
                updatedCluesSet.add("subway_log")
                unlockedMapLocations.add("warehouse")
                unlockedMapLocations.add("subway")
                showMessage("Локации 'Заброшенный Склад №5' и 'Станция Складская' разблокированы!")
            }

            var progress = current.copy(
                connectedClues = connectionsSet.joinToString(","),
                discoveredClues = updatedCluesSet.joinToString(","),
                unlockedLocations = unlockedMapLocations.joinToString(","),
                experience = finalXp,
                level = currentLvl
            )

            // Trigger some achievements
            progress = unlockAchievementDirectly(progress, "first_clue")

            val connectionsCount = connectionsSet.size
            if (connectionsCount >= 4) {
                progress = unlockAchievementDirectly(progress, "analyst")
            }
            if (connectionsCount >= 7) {
                progress = unlockAchievementDirectly(progress, "master")
            }

            repository.saveProgress(progress)
            showMessage("Правильная связь установлена! +40 XP")
        }
    }

    private fun markClueAnalyzed(clueId: String) {
        val current = _progressState.value ?: return
        val set = current.analyzedClues.split(",").filter { it.isNotEmpty() }.toMutableSet()
        if (set.contains(clueId)) return

        set.add(clueId)
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProgress(current.copy(
                analyzedClues = set.joinToString(",")
            ))
        }
    }

    // Finalize case solution with Ghost_13
    fun submitFinalAccusation(isGoodEnding: Boolean) {
        val current = _progressState.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            var progress = current.copy(
                endingType = if (isGoodEnding) "GOOD" else "MEDIUM"
            )
            progress = unlockAchievementDirectly(progress, "case_closed")
            repository.saveProgress(progress)
        }
    }

    fun triggerBadEnding() {
        val current = _progressState.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProgress(current.copy(
                endingType = "BAD"
            ))
        }
    }

    private fun unlockAchievementDirectly(progress: GameProgress, achievementId: String): GameProgress {
        val set = progress.unlockedAchievements.split(",").filter { it.isNotEmpty() }.toMutableSet()
        if (set.contains(achievementId)) return progress

        set.add(achievementId)
        showMessage("Достижение получено: ${StaticGameData.ACHIEVEMENTS.firstOrNull { it.id == achievementId }?.title ?: achievementId}!")
        return progress.copy(
            unlockedAchievements = set.joinToString(",")
        )
    }

    fun quitGame() {
        // Clear window details
        _selectedApp.value = null
        _zoomClueId.value = null
    }

    override fun onCleared() {
        super.onCleared()
    }
}
