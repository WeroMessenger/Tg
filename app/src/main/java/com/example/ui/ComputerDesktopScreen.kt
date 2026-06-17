package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.db.GameProgress
import com.example.game.GameViewModel
import com.example.ui.apps.*
import kotlinx.coroutines.delay

data class DesktopIcon(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComputerDesktopScreen(
    viewModel: GameViewModel,
    progress: GameProgress,
    onBackToMainMenu: () -> Unit
) {
    val selectedApp by viewModel.selectedApp.collectAsState()
    val dbSearchQuery by viewModel.dbSearchQuery.collectAsState()
    val hexInput by viewModel.hexInput.collectAsState()
    val hexDecryptedText by viewModel.hexDecryptedText.collectAsState()
    val gameMessage by viewModel.gameMessage.collectAsState()

    var showAccusationDialog by remember { mutableStateOf(false) }

    // Parse progress strings
    val discoveredClues = remember(progress.discoveredClues) {
        progress.discoveredClues.split(",").filter { it.isNotEmpty() }.toSet()
    }
    val connectedPairs = remember(progress.connectedClues) {
        progress.connectedClues.split(",").filter { it.isNotEmpty() }.toSet()
    }
    val unlockedLocations = remember(progress.unlockedLocations) {
        progress.unlockedLocations.split(",").filter { it.isNotEmpty() }.toSet()
    }
    val unlockedAchievements = remember(progress.unlockedAchievements) {
        progress.unlockedAchievements.split(",").filter { it.isNotEmpty() }.toSet()
    }

    // New Clue Detection State Hook to trigger custom pixel alert animations
    var activeNewClueName by remember { mutableStateOf<String?>(null) }
    var knownClues by remember { mutableStateOf(discoveredClues) }

    LaunchedEffect(discoveredClues) {
        val freshlyDiscovered = discoveredClues - knownClues
        if (freshlyDiscovered.isNotEmpty()) {
            val clueId = freshlyDiscovered.first()
            val clueTitle = com.example.game.StaticGameData.CLUES.firstOrNull { it.id == clueId }?.title ?: clueId
            activeNewClueName = clueTitle
        }
        knownClues = discoveredClues
    }

    // Interactive digital clock tick
    var clockTime by remember { mutableStateOf("07:45:00") }
    LaunchedEffect(Unit) {
        while (true) {
            val calendar = java.util.Calendar.getInstance()
            val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            clockTime = formatter.format(calendar.time)
            delay(1000)
        }
    }

    // Automatic check to clear popups
    LaunchedEffect(gameMessage) {
        if (gameMessage != null) {
            delay(3500)
            viewModel.clearGameMessage()
        }
    }

    // Accusation unlocked condition (connected HEX email to profile)
    val isAccusationAvailable = connectedPairs.contains("browser_history->encrypted_email") || discoveredClues.contains("encrypted_email")

    CrtMonitorOverlay {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RetroBgDark)
        ) {
            // MAIN SPACE: DESKTOP
            Column(modifier = Modifier.fillMaxSize()) {
                // TOP HUB STATUS BAR (Vintage terminal style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(RetroBgHeader)
                        .drawBehind {
                            val borderPx = 1.dp.toPx()
                            drawLine(
                                color = RetroNeonGreen,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = borderPx
                            )
                        }
                        .padding(horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Inspector profile tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = RetroNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    "[ОПЕРАТИВНИК]: Алексей Воронов • ОУР ГУВД",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    "КЛАСС: СЛЕДОВАТЕЛЬ-OSINT • XP: ${progress.experience}/100 • [СКАНИРОВАНИЕ]",
                                    color = RetroNeonGreen,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Achievements View Summary Banner
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            unlockedAchievements.forEach { ach ->
                                Box(
                                    modifier = Modifier
                                        .background(RetroNeonCyan.copy(alpha = 0.15f))
                                        .border(1.dp, RetroNeonCyan)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (ach == "first_clue") "🏅 СЛЕД" else if (ach == "analyst") "🏅 ОСИНТ" else "🏅 РОЗЫСК",
                                        color = RetroNeonCyan,
                                        fontSize = 7.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Ticking digital clock & power system
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "17.06.26 • $clockTime",
                                color = RetroNeonGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF261014))
                                    .border(1.dp, RetroAlertRed)
                                    .clickable { onBackToMainMenu() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Выйти из системы",
                                    tint = RetroAlertRed,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // GRID OF APPLICATIONS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Shaded police star background
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = RetroNeonGreen.copy(alpha = 0.04f),
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    val desktopIcons = listOf(
                        DesktopIcon("dossier", "Досье", Icons.Default.AssignmentInd, RetroNeonGreen),
                        DesktopIcon("database", "МВД База", Icons.Default.Storage, RetroNeonGreen),
                        DesktopIcon("map", "Map-Радар", Icons.Default.Map, RetroNeonGreen),
                        DesktopIcon("socials", "VK-Talk", Icons.Default.People, RetroNeonGreen),
                        DesktopIcon("cameras", "Cam-Поток", Icons.Default.Videocam, RetroNeonCyan),
                        DesktopIcon("gallery", "Фото-Зум", Icons.Default.Collections, RetroNeonCyan),
                        DesktopIcon("mail", "SecureMail", Icons.Default.Email, RetroNeonGreen),
                        DesktopIcon("chat", "Оперативный", Icons.Default.Forum, RetroNeonGreen),
                        DesktopIcon("investigate", "Пульт Улик", Icons.Default.ViewAgenda, RetroAlertRed)
                    )

                    // Compact, landscape-friendly desktop layout
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .align(Alignment.Center)
                    ) {
                        items(desktopIcons) { app ->
                            Box(
                                modifier = Modifier
                                    .height(68.dp)
                                    .background(RetroBgMedium)
                                    .border(1.dp, if (app.id == "investigate") RetroAlertRed else RetroBorderLine)
                                    .clickable { viewModel.selectApp(app.id) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = app.icon,
                                        contentDescription = app.label,
                                        tint = app.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = app.label.uppercase(),
                                        color = Color.White,
                                        fontSize = 8.8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // ACCUSATION ACCELERATOR ACTUATOR KEY
                    if (isAccusationAvailable) {
                        RetroButton(
                            onClick = { showAccusationDialog = true },
                            buttonColor = Color(0xFF330910),
                            borderColor = RetroAlertRed,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = RetroAlertRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("АРЕСТ GHOST_13", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // DYNAMIC DETECTED NEW CLUE FLOATING ACCELERATION PROMPT
            if (activeNewClueName != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NewClueAlert(
                        clueName = activeNewClueName!!,
                        onDismiss = { activeNewClueName = null }
                    )
                }
            }

            // APPS RETRO GRAPHIC FLOATING SIMULATION CONTAINER WINDOW
            if (selectedApp != null) {
                val appKey = selectedApp!!
                val appTitle = when(appKey) {
                    "dossier" -> "SYS:ЛИЧНЫЕ ДЕЛА ПРОПАВШИХ"
                    "database" -> "SYS:АРХИВ МВД [СЕКТОР-OSINT]"
                    "map" -> "GPS:СПУТНИКОВЫЙ РАДАР ГУВД"
                    "socials" -> "WEB:VKTALK СОЦИАЛЬНЫЙ НАБЛЮДАТЕЛЬ"
                    "cameras" -> "STREAM:КАМЕРЫ ВИДЕОНАБЛЮДЕНИЯ ГУВД"
                    "gallery" -> "ANA:ФОТО-АНАЛИЗАТОР СУДЕБНЫХ ВЕЩДОКОВ"
                    "mail" -> "SECUREMAIL:КЛИЕНТ ВНУТРЕННЕЙ СВЯЗИ"
                    "chat" -> "ТЕРМИНАЛ:ДЕЖУРСТВО ПО СВЯЗИ РФ"
                    "investigate" -> "ПУЛЬТ:МАРКЕРНАЯ ДОСКА ДЕДУКЦИИ"
                    else -> "ПРИЛОЖЕНИЕ"
                }

                // Dark cover layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.72f))
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Clean Retro Terminal window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(RetroBgDark)
                            .border(1.5.dp, if (appKey == "investigate") RetroAlertRed else RetroNeonGreen)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Custom window handle bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .background(RetroBgHeader)
                                    .drawBehind {
                                        drawLine(
                                            color = if (appKey == "investigate") RetroAlertRed else RetroNeonGreen,
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(if (appKey == "investigate") RetroAlertRed else RetroNeonGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = appTitle,
                                        color = if (appKey == "investigate") RetroAlertRed else RetroNeonGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Interactive sharp Close Box
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(0xFF261014))
                                        .border(1.dp, RetroAlertRed)
                                        .clickable { viewModel.selectApp(null) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "X",
                                        color = RetroAlertRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Active app viewport content frame
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when (appKey) {
                                    "dossier" -> DossierApp(viewModel, discoveredClues)
                                    "database" -> DatabaseApp(viewModel, dbSearchQuery)
                                    "map" -> MapApp(viewModel, unlockedLocations)
                                    "socials" -> SocialsApp(viewModel)
                                    "cameras" -> CamerasApp(viewModel, discoveredClues)
                                    "gallery" -> GalleryApp(viewModel, discoveredClues)
                                    "mail" -> MailApp(viewModel, hexInput, hexDecryptedText)
                                    "chat" -> ChatApp(viewModel, progress.chatHistory)
                                    "investigate" -> InvestigateBoardApp(
                                        viewModel = viewModel,
                                        discoveredClueIds = discoveredClues,
                                        connectedPairs = connectedPairs,
                                        firstSelectedClue = viewModel.firstSelectedBoardClue.collectAsState().value,
                                        difficulty = progress.difficulty
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECURE CONVICTION DIALOG
            if (showAccusationDialog) {
                Dialog(onDismissRequest = { showAccusationDialog = false }) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        RetroCard(
                            borderColor = RetroAlertRed,
                            backgroundColor = RetroBgDark,
                            glow = true
                        ) {
                            Text(
                                "ФОРМИРОВАНИЕ ОРДЕРА: КТО GHOST_13?",
                                color = RetroAlertRed,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                "Вы собрали достаточный массив цифровых улик. Спецслужбы ждут финального ордера. Кем на самом деле является агент Ghost_13?",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                AccuseOptionCard(
                                    title = "Капитан Андрей Кузьмин",
                                    desc = "Бывший спец-начальник ИТ-отдела розыска. Отрежиссировал это дело как дерзкий урок ленивым оперативникам.",
                                    onClick = {
                                        viewModel.submitFinalAccusation(isGoodEnding = true)
                                        showAccusationDialog = false
                                    }
                                )
                                AccuseOptionCard(
                                    title = "Информатор Крот",
                                    desc = "Даркнет-информатор. Вёл двойную игру, чтобы отвлечь подозрение полиции от себя.",
                                    onClick = {
                                        viewModel.submitFinalAccusation(isGoodEnding = false)
                                        showAccusationDialog = false
                                    }
                                )
                                AccuseOptionCard(
                                    title = "Дмитрий Петрович (Начальник)",
                                    desc = "Начальник отдела. Инициировал похищения, чтобы за выкуп сорвать куш и уйти в отставку.",
                                    onClick = {
                                        viewModel.triggerBadEnding()
                                        showAccusationDialog = false
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                RetroButton(
                                    onClick = { showAccusationDialog = false },
                                    buttonColor = RetroBgHeader,
                                    borderColor = Color.Gray,
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("ОТМЕНА", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }

            // GENERAL SYSTEM MESSAGE HUD TOAST
            if (gameMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .background(RetroBgMedium)
                            .border(1.5.dp, RetroNeonGreen)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = RetroNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = gameMessage!!.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // FULLSCREEN GAME ENDING OVERLAYS
            if (progress.endingType.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RetroBgDark)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RetroCard(
                        borderColor = if (progress.endingType == "GOOD") RetroNeonGreen else RetroAlertRed,
                        backgroundColor = RetroBgMedium,
                        glow = true,
                        modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize().padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (progress.endingType == "GOOD") Icons.Default.Star else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (progress.endingType == "GOOD") RetroNeonGreen else RetroAlertRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val endingTitle = when(progress.endingType) {
                                "GOOD" -> "ОТЛИЧНЫЙ ФИНАЛ: ЧЕСТЬ СПАСЕНА!"
                                "MEDIUM" -> "НЕЙТРАЛЬНЫЙ КОНЕЦ: ПРЕСТУПНИК ПОЙМАН"
                                else -> "ПЛОХОЙ ФИНАЛ: ДЕЯНИЕ НЕ РАСКРЫТО"
                            }

                            val endingDesc = when(progress.endingType) {
                                "GOOD" -> "Полиция взяла заброшенный Склад №5 штурмом! Вы выявили личность бывшего капитана Кузьмина (Ghost_13). Все заложники — Анна, таксист Дмитрий и Марина невредимы. Кузьмин заключен в СИЗО до суда. Руководство признает Ваш безоговорочный успех: Вам досрочно присвоено высшее следовательское звание!\n\nВы с успехом освоили OSINT!"
                                "MEDIUM" -> "Капитан Кузьмин пойман силами спецназа. Но вы промедлили с анализом ворот завода и вовремя не определили координаты заброшки. К сожалению, часть похищенных людей осталась не вызволена вовремя. Вас оставили в роли дежурного стажёра.\n\nПопробуйте пройти сложный режим!"
                                else -> "Ваша ложная улика разрушила следствие! Капитан Кузьмин оперативно стёр все файлы и улетел за границу со всей добычей. Дело официально закрыли, а Вас со скандалом уволили из органов розыска без выходного пособия.\n\nПопробуйте ещё раз составить связи!"
                            }

                            Text(
                                text = endingTitle,
                                color = if (progress.endingType == "GOOD") RetroNeonGreen else RetroAlertRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = endingDesc,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            RetroButton(
                                onClick = onBackToMainMenu,
                                buttonColor = RetroBgHeader,
                                borderColor = if (progress.endingType == "GOOD") RetroNeonGreen else RetroAlertRed,
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("ВЕРНУТЬСЯ В ГЛАВНОЕ МЕНЮ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccuseOptionCard(
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF260D11))
            .border(1.dp, RetroAlertRed.copy(alpha = 0.5f))
            .padding(10.dp)
    ) {
        Column {
            Text(
                title.uppercase(),
                color = RetroAlertRed,
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
