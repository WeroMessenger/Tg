package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import com.example.ui.*

data class CameraFeed(
    val id: String,
    val title: String,
    val status: String,
    val timestamp: String,
    val description: String,
    val isClueUnlocked: Boolean = false
)

@Composable
fun CamerasApp(
    viewModel: GameViewModel,
    discoveredClues: Set<String>
) {
    val cameras = listOf(
        CameraFeed("cam_1", "КАМЕРА 01: ЗАВОД ВОРОТА", "ОФФЛАЙН", "10 ИЮНЯ 23:45", "Сенсор отключен. Оборудование повреждено."),
        CameraFeed("cam_2", "КАМЕРА 02: ВХОД КРАСНОГО ОКТЯБРЯ", "ОФФЛАЙН", "10 ИЮНЯ 23:55", "Сигнал отсутствует. Кабель перерезан."),
        CameraFeed("cam_3", "КАМЕРА 03: МЕТРО КРАСНЫЕ ВОРОТА", "ОНЛАЙН", "15 ИЮНЯ 18:02", "Пассажиропоток в норме. Марина Соколова зафиксирована входящей в черном пуховике."),
        CameraFeed("cam_4", "КАМЕРА 04: ПЕРЕЕЗД РОЗЫСКА", "ОНЛАЙН (АРХИВ)", "12 ИЮНЯ 02:46", "Внимание: Зафиксировано такси Hyundai. Из водительской двери выходит Дмитрий Крылов, за ним следует высокий мужчина в темной куртке с армейским шевроном.", isClueUnlocked = true)
    )

    var activeCam by remember { mutableStateOf<CameraFeed?>(null) }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // Left Column: Monitor view or camera feeds grid
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.48f)
                .background(Color(0xFF070B14))
                .border(1.dp, RetroBorderLine)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.size(5.dp).background(RetroNeonCyan))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "ВИДЕОСЕРВЕР ПОТОКОВ ГУВД",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cameras) { cam ->
                    val isSelected = activeCam?.id == cam.id
                    val borderTint = if (isSelected) RetroNeonGreen else RetroBorderLine
                    val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(bgTint)
                            .border(1.dp, borderTint)
                            .clickable { activeCam = cam }
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
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (cam.status.contains("ОНЛАЙН")) RetroNeonGreen else RetroAlertRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = cam.status,
                                    color = if (cam.status == "ОНЛАЙН") RetroNeonGreen else if (cam.status.contains("АРХИВ")) RetroOrange else RetroAlertRed,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = cam.title,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                Text(
                                    text = cam.timestamp,
                                    color = Color.Gray,
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Live Feed Screen representation
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val cam = activeCam
            if (cam != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Screen Simulator Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                            .background(Color.Black)
                            .border(1.dp, RetroNeonCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing CCTV Interlacing scanlines
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val lineGap = 12.dp.toPx()
                            for (y in 0..size.height.toInt() step lineGap.toInt()) {
                                drawLine(
                                    color = RetroNeonCyan.copy(alpha = 0.08f),
                                    start = Offset(0f, y.toFloat()),
                                    end = Offset(size.width, y.toFloat()),
                                    strokeWidth = 1f
                                )
                            }
                            // CCTV Crosshair center marks
                            drawLine(
                                color = RetroAlertRed.copy(alpha = 0.4f),
                                start = Offset(size.width / 2 - 15, size.height / 2),
                                end = Offset(size.width / 2 + 15, size.height / 2)
                            )
                            drawLine(
                                color = RetroAlertRed.copy(alpha = 0.4f),
                                start = Offset(size.width / 2, size.height / 2 - 15),
                                end = Offset(size.width / 2, size.height / 2 + 15)
                            )
                        }

                        // Graphics screen simulations
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔴 REC • ${cam.timestamp}",
                                    color = RetroAlertRed,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SEC: ${cam.id.uppercase()}",
                                    color = RetroNeonCyan,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            if (cam.id == "cam_4") {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(1.dp, RetroNeonCyan)
                                        .background(RetroNeonCyan.copy(alpha = 0.06f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = RetroNeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "ЛИЦО: КРЫЛОВ",
                                            color = RetroNeonCyan,
                                            fontSize = 7.5.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "ЦЕНТРАЛЬНОЕ ПРЕВЬЮ ГУВД СУБЪЕКТА РФ",
                                color = RetroNeonGreen,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    // Feed description Details
                    RetroCard(
                        borderColor = RetroNeonCyan,
                        backgroundColor = RetroBgMedium,
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Text(
                            "ОПЕРАТИВНОЕ СОПРОВОЖДЕНИЕ КАДРА:",
                            color = RetroNeonCyan,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            cam.description,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )

                        if (cam.isClueUnlocked) {
                            Spacer(modifier = Modifier.height(4.dp))
                            RetroButton(
                                onClick = {
                                    viewModel.zoomClue("camera_station")
                                    viewModel.showMessage("Фрагмент кадра добавлен на Доску улик!")
                                },
                                buttonColor = RetroBgHeader,
                                borderColor = RetroNeonGreen,
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                              ) {
                                Text("ЭКСПОРТ ДАННЫХ НА ПУЛЬТ УЛИК", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                              }
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        tint = RetroBorderLine,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ВЫБЕРИТЕ ОДНУ ИЗ КАМЕР СЛЕВА ДЛЯ ВЫВОДА СТРИМА",
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
