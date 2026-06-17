package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.ui.ComputerDesktopScreen
import com.example.ui.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val gameViewModel: GameViewModel = viewModel()
        val progressState by gameViewModel.progressState.collectAsState()

        var currentScreen by remember { mutableStateOf("menu") }

        // Automatically route to desk if a loaded game is in progress
        LaunchedEffect(progressState) {
          if (progressState != null && progressState!!.isGameInProgress && currentScreen == "menu") {
            currentScreen = "desktop"
          }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
              "menu" -> {
                MainMenuScreen(
                  viewModel = gameViewModel,
                  progress = progressState,
                  onStartGame = { currentScreen = "desktop" },
                  onContinueGame = { currentScreen = "desktop" },
                  onExitApp = { finish() }
                )
              }
              "desktop" -> {
                val progress = progressState
                if (progress != null) {
                  ComputerDesktopScreen(
                    viewModel = gameViewModel,
                    progress = progress,
                    onBackToMainMenu = {
                      gameViewModel.quitGame()
                      currentScreen = "menu"
                    }
                  )
                } else {
                  currentScreen = "menu"
                }
              }
            }
          }
        }
      }
    }
  }
}


