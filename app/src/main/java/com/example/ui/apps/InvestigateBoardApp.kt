package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ClueItem
import com.example.game.ClueType
import com.example.game.GameViewModel
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun InvestigateBoardApp(
    viewModel: GameViewModel,
    discoveredClueIds: Set<String>,
    connectedPairs: Set<String>,
    firstSelectedClue: String?,
    difficulty: String
) {
    // Collect all discovered clue models
    val activeClues = remember(discoveredClueIds) {
        StaticGameData.CLUES.filter { discoveredClueIds.contains(it.id) }
    }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: CLUES PALETTE
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .background(Color(0xFF0D0B10)) // Heavy dark crime desk tint
                .border(1.dp, RetroAlertRed)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(RetroAlertRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "МАРКЕРНЫЙ СТЕНД СВЯЗЕЙ",
                        color = RetroAlertRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "[УЗЛОВ: ${discoveredClueIds.size}]",
                    color = Color.LightGray,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (difficulty == "EASY") {
                Text(
                    "ПОДСКАЗКА: Связывайте ДОСЬЕ -> ЧАТЫ -> ВЕЩДОКИ -> КОРДИНАТЫ",
                    color = RetroNeonCyan,
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // High performance grid of clue nodes
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                gridItems(activeClues) { clue ->
                    val isFirstSelected = firstSelectedClue == clue.id
                    val isLinked = connectedPairs.any { it.contains(clue.id) }

                    val borderTint = when {
                        isFirstSelected -> RetroNeonGreen
                        isLinked -> RetroBorderLine
                        else -> Color.Gray.copy(alpha = 0.4f)
                    }

                    val bgTint = when {
                        isFirstSelected -> Color(0xFF102619)
                        isLinked -> Color(0xFF16090B)
                        else -> RetroBgMedium
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .background(bgTint)
                            .border(1.dp, borderTint)
                            .clickable { viewModel.selectBoardClue(clue.id) }
                            .padding(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when(clue.type) {
                                        ClueType.DOSSIER -> Icons.Default.Assignment
                                        ClueType.IMAGE -> Icons.Default.Collections
                                        ClueType.ENCRYPTED -> Icons.Default.LockOpen
                                        else -> Icons.Default.HelpOutline
                                    },
                                    contentDescription = null,
                                    tint = if (isFirstSelected) RetroNeonGreen else RetroAlertRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                if (isLinked) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = RetroNeonGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = clue.title.uppercase(),
                                    color = if (isFirstSelected) RetroNeonGreen else Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                Text(
                                    text = clue.summary,
                                    color = Color.LightGray,
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2,
                                    lineHeight = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHT COL: CONNECTED PAIRS LOG LISTING
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).background(RetroNeonGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "АКТИВНЫЕ НИТИ ДЕДУКЦИИ",
                    color = RetroNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Current connections logs
            if (connectedPairs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, RetroBorderLine)
                        .background(RetroBgMedium)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "НЕТ АКТИВНЫХ СВЯЗЕЙ.\n\nКликните по одному следу слева, затем выберите другой для проведения нити дедукции.",
                        color = Color.Gray,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(connectedPairs.toList()) { pair ->
                        val keys = pair.split("->")
                        val fromId = keys.getOrNull(0) ?: ""
                        val toId = keys.getOrNull(1) ?: ""

                        val fromName = StaticGameData.CLUES.firstOrNull { it.id == fromId }?.title ?: fromId
                        val toName = StaticGameData.CLUES.firstOrNull { it.id == toId }?.title ?: toId

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RetroBgMedium)
                                .border(1.dp, RetroBorderLine)
                                .padding(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = RetroNeonGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Column {
                                    Text(
                                        fromName.uppercase(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "══ СВЯЗАНО С ══",
                                        color = RetroAlertRed,
                                        fontSize = 7.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        toName.uppercase(),
                                        color = RetroNeonCyan,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // High priority custom node connection state widget
            if (firstSelectedClue != null) {
                val clueObj = StaticGameData.CLUES.firstOrNull { it.id == firstSelectedClue }
                if (clueObj != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1B24))
                            .border(1.dp, RetroNeonCyan)
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BlinkingCursor(color = RetroNeonCyan, char = "▮")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "ВЫБРАН ПЕРВЫЙ УЗЕЛ:",
                                    color = RetroNeonCyan,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                clueObj.title.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Кликните второй узел слева для соединения.",
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
