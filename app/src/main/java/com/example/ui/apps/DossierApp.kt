package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ClueItem
import com.example.game.GameViewModel
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun DossierApp(
    viewModel: GameViewModel,
    discoveredClueIds: Set<String>
) {
    var selectedClue by remember { mutableStateOf<ClueItem?>(null) }
    val dossiers = StaticGameData.CLUES.filter { it.type == com.example.game.ClueType.DOSSIER }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFTSIDE: SECTOR FILE LIST
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.44f)
                .background(Color(0xFF060910))
                .border(1.dp, RetroBorderLine)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.size(5.dp).background(RetroNeonGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "БАНК ДАННЫХ [ЛИЧНЫЕ ДЕЛА]",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(dossiers) { dossier ->
                    val isDiscovered = discoveredClueIds.contains(dossier.id)
                    val isSelected = selectedClue?.id == dossier.id

                    val borderTint = when {
                        isSelected -> RetroNeonGreen
                        isDiscovered -> RetroBorderLine
                        else -> Color.DarkGray.copy(alpha = 0.5f)
                    }

                    val bgTint = when {
                        isSelected -> Color(0xFF101B2E)
                        isDiscovered -> RetroBgMedium
                        else -> Color(0xFF020408)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgTint)
                            .border(1.dp, borderTint)
                            .clickable(enabled = isDiscovered) { selectedClue = dossier }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDiscovered) Icons.Default.AssignmentInd else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isDiscovered) RetroNeonGreen else Color.DarkGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isDiscovered) dossier.title.uppercase() else "[ЗАБЛОКИРОВАНО]",
                                    color = if (isDiscovered) Color.White else Color.DarkGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isDiscovered) dossier.summary else "Следствие не обнаружило личные метаданные",
                                    color = if (isDiscovered) Color.LightGray else Color.Gray,
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHTSIDE: COMPREHENSIVE CRIMINAL DOSSIER SHEET
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val dossier = selectedClue
            if (dossier != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = dossier.title.uppercase(),
                            color = RetroNeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        RetroCard(
                            borderColor = RetroNeonGreen,
                            backgroundColor = RetroBgMedium
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BlinkingCursor(color = RetroNeonGreen, char = "▮")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "СПРАВОЧНАЯ СВОДКА МВД:",
                                    color = RetroNeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                dossier.description,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                            )

                            Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                "КАТАЛОГИЗАЦИЯ ЦИФРОВЫХ УЛИК:",
                                color = RetroNeonCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                dossier.details,
                                color = Color.LightGray,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        RetroButton(
                            onClick = { viewModel.zoomClue(dossier.id) },
                            buttonColor = RetroBgHeader,
                            borderColor = RetroNeonGreen,
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text("ПРОАНАЛИЗИРОВАТЬ ФАЙЛ КРУПНЫМ ПЛАНОМ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = null,
                        tint = RetroBorderLine,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ВЫБЕРИТЕ ОТКРЫТОЕ ДЕЛО В КОЛОНКЕ СЛЕВА",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
