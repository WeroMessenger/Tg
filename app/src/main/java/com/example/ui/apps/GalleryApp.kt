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
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.ZoomIn
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
import com.example.game.ClueItem
import com.example.game.GameViewModel
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun GalleryApp(
    viewModel: GameViewModel,
    discoveredClues: Set<String>
) {
    // Collect all image clues
    val images = StaticGameData.CLUES.filter {
        it.type == com.example.game.ClueType.IMAGE || it.id == "factory_photo" || it.id == "camera_station"
    }

    var selectedImageClue by remember { mutableStateOf<ClueItem?>(null) }
    var deepScanEnabled by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: PHOTOS THUMBNAILS
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.42f)
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
                    "ФОРЕНЗИК ФОТО-АРХИВ",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(images) { item ->
                    val isDiscovered = discoveredClues.contains(item.id) || item.isRevealedByDefault
                    val isSelected = selectedImageClue?.id == item.id
                    val borderTint = if (isSelected) RetroNeonGreen else if (isDiscovered) RetroBorderLine else Color.DarkGray.copy(alpha = 0.5f)
                    val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgTint)
                            .border(1.dp, borderTint)
                            .clickable(enabled = isDiscovered) {
                                selectedImageClue = item
                                deepScanEnabled = false
                            }
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isDiscovered) item.title.uppercase() else "[БЛОКИРОВАНО]",
                                color = if (isDiscovered) Color.White else Color.DarkGray,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isDiscovered) item.summary else "Информация заблокирована",
                                color = if (isDiscovered) Color.LightGray else Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // RIGHT COL: DETAILED PHOTO PREVIEW WITH HIGH RESOLUTION ZOOM RADAR
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val imgClue = selectedImageClue
            if (imgClue != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Simulated High Resolution image Box with deep analyze trigger
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                            .background(Color.Black)
                            .border(1.dp, RetroNeonCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draws a high-tech scope background
                            drawCircle(
                                color = RetroNeonCyan.copy(alpha = 0.08f),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.height / 3f,
                                style = Stroke(width = 1f)
                            )
                            drawCircle(
                                color = RetroNeonCyan.copy(alpha = 0.03f),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.height / 1.7f,
                                style = Stroke(width = 1f)
                            )
                        }

                        // Text Representation of image scan grid
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ОБРАБОТЧИК: DEEP_SCAN_MATRIX v1.5",
                                    color = RetroNeonCyan,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ЗУМ: ${if (deepScanEnabled) "400% [СПЕКТР]" else "100%"}",
                                    color = Color.Gray,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            if (deepScanEnabled) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.95f)
                                        .background(RetroBgMedium)
                                        .border(1.dp, RetroOrange)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BlinkingCursor(color = RetroOrange, char = "⚠")
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "МЕТАДАННЫЕ И ТЕКСТ СНИМКА:",
                                                color = RetroOrange,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            imgClue.details,
                                            color = Color.White,
                                            fontSize = 9.5.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = null,
                                        tint = RetroBorderLine,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(
                                        "ИЗУЧИТЕ МЕТАДАННЫЕ СНИМКА НА ПРЕДМЕТ СКРЫТЫХ УЛИК",
                                        color = Color.Gray,
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "СПУТНИК-МАТРИЦА ГУВД • РАЗРЕШЕНИЕ: 4096 x 3112 PX",
                                color = RetroNeonGreen,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    // Controller Action Buttons
                    RetroCard(
                        borderColor = RetroNeonCyan,
                        backgroundColor = RetroBgMedium,
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Text(
                            "СУДЕБНАЯ СПРАВКА ФАЙЛА:",
                            color = RetroNeonCyan,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            imgClue.description,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RetroButton(
                                onClick = {
                                    deepScanEnabled = !deepScanEnabled
                                    if (deepScanEnabled) {
                                        viewModel.zoomClue(imgClue.id)
                                    }
                                },
                                buttonColor = if (deepScanEnabled) Color(0xFF261014) else RetroBgHeader,
                                borderColor = if (deepScanEnabled) RetroAlertRed else RetroNeonGreen,
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (deepScanEnabled) Icons.Default.SettingsBackupRestore else Icons.Default.ZoomIn,
                                    contentDescription = null,
                                    tint = if (deepScanEnabled) RetroAlertRed else RetroNeonGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (deepScanEnabled) "СБРОС СПЕКТРА" else "СПЕКТРАЛЬНЫЙ СКАН",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                            }

                            if (deepScanEnabled && imgClue.id == "factory_photo" && !discoveredClues.contains("factory_coords")) {
                                RetroButton(
                                    onClick = {
                                        viewModel.zoomClue("factory_coords")
                                        viewModel.showMessage("Выявлены координаты! Код: 55.733, 37.601 добавлено!")
                                    },
                                    buttonColor = Color(0xFF101B2E),
                                    borderColor = RetroNeonCyan,
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Cached, contentDescription = null, tint = RetroNeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ЭКСПОРТ КООРДИНАТ", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
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
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = RetroBorderLine,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ВЫБЕРИТЕ СНИМОК ДЛЯ АНАЛИЗА В КОЛОНКЕ СЛЕВА",
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
