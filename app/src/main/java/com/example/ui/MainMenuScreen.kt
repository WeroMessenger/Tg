package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.game.GameViewModel

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    progress: com.example.db.GameProgress?,
    onStartGame: () -> Unit,
    onContinueGame: () -> Unit,
    onExitApp: () -> Unit
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    CrtMonitorOverlay {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RetroBgDark)
        ) {
            // Background Animation: falling rain drops
            PixelBackdropRain(modifier = Modifier.fillMaxSize())

            // Main Interactive Row/Column structure optimized for horizontal smartphone layouts
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE: Retro Department Logo & Glowing Interactive Header
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Vintage pixelated police logo block
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(110.dp)
                            .background(RetroBgMedium)
                            .border(1.dp, RetroNeonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.img_retro_cyber_banner),
                            contentDescription = "Cyber workstation",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ОСИНТ",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetroNeonGreen,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BlinkingCursor(color = RetroNeonGreen, char = "_")
                    }

                    Text(
                        text = "ПОСЛЕДНИЙ СЛЕД",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "РЕТРО-ТЕРМИНАЛ ГУВД v1.2",
                        fontSize = 10.sp,
                        color = RetroNeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Офлайн-симулятор OSINT-расследования",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // RIGHT SIDE: Large, touch-friendly tactile mechanical buttons
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // New game key
                    RetroButton(
                        onClick = { showDifficultyDialog = true },
                        buttonColor = RetroBgHeader,
                        borderColor = RetroNeonGreen,
                        glow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = RetroNeonGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "НАЧАТЬ РАСЛЕДОВАНИЕ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    val hasSavedGame = progress != null && progress.isGameInProgress
                    RetroButton(
                        onClick = { if (hasSavedGame) onContinueGame() },
                        enabled = hasSavedGame,
                        buttonColor = RetroBgHeader,
                        borderColor = if (hasSavedGame) RetroNeonCyan else Color.DarkGray,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (hasSavedGame) RetroNeonCyan else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ПРОДОЛЖИТЬ СЛЕДСТВИЕ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (hasSavedGame) Color.White else Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    RetroButton(
                        onClick = { showSettingsDialog = true },
                        buttonColor = RetroBgHeader,
                        borderColor = RetroBorderLine,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color.LightGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "СПРАВКА И ИНФОРМАЦИЯ",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    RetroButton(
                        onClick = onExitApp,
                        buttonColor = Color(0xFF241014),
                        borderColor = RetroAlertRed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = RetroAlertRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ВЫКЛЮЧИТЬ ПАКЕТ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = RetroAlertRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }

    // DIFFICULTY SELECT DIALOG
    if (showDifficultyDialog) {
        Dialog(
            onDismissRequest = { showDifficultyDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
            ) {
                RetroCard(
                    borderColor = RetroNeonGreen,
                    backgroundColor = RetroBgDark,
                    glow = true
                ) {
                    Text(
                        text = "ВЫБЕРИТЕ РЕЖИМ РАСШИФРОВКИ",
                        color = RetroNeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Side-by-side or stacked grid depending on height
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyCard(
                            title = "ЛЕГКИЙ РЕЖИМ [СТАЖЕР]",
                            desc = "Маркерный пульт с авто-подсказками нитей. Идеально для первого ознакомления.",
                            diffColor = RetroNeonCyan,
                            onClick = {
                                viewModel.startNewGame("EASY")
                                showDifficultyDialog = false
                                onStartGame()
                            }
                        )
                        DifficultyCard(
                            title = "СРЕДНИЙ РЕЖИМ [ИНСПЕКТОР]",
                            desc = "Стандартная дедукция. Вы связываете все следы, координаты и файлы самостоятельно.",
                            diffColor = RetroOrange,
                            onClick = {
                                viewModel.startNewGame("MEDIUM")
                                showDifficultyDialog = false
                                onStartGame()
                            }
                        )
                        DifficultyCard(
                            title = "СЛОЖНЫЙ РЕЖИМ [ПРОФЕССИОНАЛ]",
                            desc = "Настоящий OSINT-специалист. Ошибки режут очки репутации, авто-подсказки заблокированы.",
                            diffColor = RetroAlertRed,
                            onClick = {
                                viewModel.startNewGame("HARD")
                                showDifficultyDialog = false
                                onStartGame()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RetroButton(
                            onClick = { showDifficultyDialog = false },
                            buttonColor = Color(0xFF1D1418),
                            borderColor = Color.Gray,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("ОТМЕНА", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    // SETTINGS/INFO DIALOG
    if (showSettingsDialog) {
        Dialog(
            onDismissRequest = { showSettingsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .padding(16.dp)
            ) {
                RetroCard(
                    borderColor = RetroNeonCyan,
                    backgroundColor = RetroBgDark
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ДИРЕКТИВА ПОЛЬЗОВАТЕЛЯ • OSINT DETECTIVE",
                            color = RetroNeonCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        IconButton(onClick = { showSettingsDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White)
                        }
                    }

                    Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "СИСТЕМА: Последний След — это симулятор расследования с использованием OSINT (методов поиска по открытым источникам/данным).",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "РУКОВОДСТВО К ДЕЙСТВИЮ:",
                            fontWeight = FontWeight.Bold,
                            color = RetroNeonCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            "• ДОСЬЕ И ТЕРМИНАЛ: Изучайте личные дела пропавших в Досье и МВД Архиве.\n" +
                            "• ПОЧТА И СОЦСЕТИ: Расшифровывайте Hex-письма похитителя и ведите чаты с информаторами.\n" +
                            "• ГАЛЕРЕЯ: Кропотливо сверяйте геоданные и увеличивайте снимки на предмет скрытых улик.\n" +
                            "• КАРТА: Фиксируйте спутниковые координаты и выдвигайтесь на объекты.\n" +
                            "• МАРКЕРНАЯ ДОСКА: Соединяйте карточки улик нитью. Верно выстроенный ассоциативный след открывает продвижение по делу и новые адреса!",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "СПЕЦ-УКАЗАНИЕ:",
                            fontWeight = FontWeight.Bold,
                            color = RetroAlertRed,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            "Все имена и адреса вымышленны. Сценарии тренировочные. Программа работает полностью автономно без подключения к серверам.",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RetroButton(
                            onClick = { showSettingsDialog = false },
                            buttonColor = RetroBgHeader,
                            borderColor = RetroNeonCyan,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("ПРИНЯТЬ", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyCard(
    title: String,
    desc: String,
    diffColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(RetroBgMedium)
            .border(1.dp, diffColor.copy(alpha = 0.5f))
            .padding(10.dp)
    ) {
        Column {
            Text(
                title,
                color = diffColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                desc,
                color = Color.LightGray,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)
