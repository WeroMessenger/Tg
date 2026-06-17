package com.example.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Tv
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
import com.example.game.MapLocation
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun MapApp(
    viewModel: GameViewModel,
    unlockedLocations: Set<String>
) {
    var selectedLocation by remember { mutableStateOf<MapLocation?>(null) }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: ACCELERATED GRID CITY RADAR CO-ORDINATE MATRICES
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.53f)
                .background(Color(0xFF020610))
                .border(1.dp, RetroBorderLine)
        ) {
            // Drawn Grid and Nodes
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStep = 32.dp.toPx()
                val lineStroke = 1f

                // Draw phosphor green radar grids
                for (x in 0..size.width.toInt() step gridStep.toInt()) {
                    drawLine(
                        color = RetroNeonGreen.copy(alpha = 0.08f),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = lineStroke
                    )
                }
                for (y in 0..size.height.toInt() step gridStep.toInt()) {
                    drawLine(
                        color = RetroNeonGreen.copy(alpha = 0.08f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = lineStroke
                    )
                }

                // Draw radar concentric circles
                drawCircle(
                    color = RetroNeonGreen.copy(alpha = 0.12f),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.height / 3,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = RetroNeonCyan.copy(alpha = 0.06f),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.height / 1.6f,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Radar elements placements using dynamic percentage BoxWithConstraints
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val outerWidth = maxWidth
                val outerHeight = maxHeight

                StaticGameData.MAP_LOCATIONS.forEach { loc ->
                    val isUnlocked = unlockedLocations.contains(loc.id) || loc.id == "home"

                    // Multipliers representing spatial coordinates on a compact 2D plane
                    val dx = when(loc.id) {
                        "home" -> 0.48f
                        "factory" -> 0.15f
                        "railway" -> 0.78f
                        "subway" -> 0.22f
                        "warehouse" -> 0.60f
                        else -> 0.5f
                    }
                    val dy = when(loc.id) {
                        "home" -> 0.42f
                        "factory" -> 0.22f
                        "railway" -> 0.15f
                        "subway" -> 0.72f
                        "warehouse" -> 0.62f
                        else -> 0.5f
                    }

                    // Place items dynamically without clipping
                    Box(
                        modifier = Modifier
                            .offset(
                                x = outerWidth * dx - 16.dp,
                                y = outerHeight * dy - 16.dp
                            )
                            .size(32.dp)
                            .background(
                                if (isUnlocked) RetroNeonGreen.copy(alpha = 0.15f) else Color.DarkGray.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isUnlocked) RetroNeonGreen else Color.DarkGray
                            )
                            .clickable { selectedLocation = loc },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (loc.id == "home") Icons.Default.MyLocation else Icons.Default.LocationOn,
                            contentDescription = loc.name,
                            tint = if (isUnlocked) RetroNeonGreen else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Map HUD status indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(RetroBgMedium)
                    .border(1.dp, RetroNeonGreen)
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        "КАРТОГРАФИЧЕСКИЙ РАДАР ГУВД",
                        color = RetroNeonGreen,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "ГЛОНАСС-СВЯЗЬ: АКТИВНА • ТОЧЕК: ${StaticGameData.MAP_LOCATIONS.size}",
                        color = Color.LightGray,
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // RIGHT COL: DETAILED TECHNICAL INVESTIGATION LOG FOR LOCKED/UNLOCKED GEOCORDS
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp)
        ) {
            val loc = selectedLocation
            if (loc != null) {
                val isUnlocked = unlockedLocations.contains(loc.id) || loc.id == "home"

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = loc.name.uppercase(),
                            color = if (isUnlocked) RetroNeonGreen else Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "КООРДИНАТЫ: ${loc.lat}, ${loc.lng} • ТИП: ${loc.type}",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 1.dp, bottom = 4.dp)
                        )

                        RetroCard(
                            borderColor = if (isUnlocked) RetroNeonGreen else Color.DarkGray,
                            backgroundColor = RetroBgMedium
                        ) {
                            if (isUnlocked) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = RetroNeonGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "СПРАВОЧНЫЕ ДАННЫЕ О Б Ъ Е К Т А :",
                                        color = RetroNeonGreen,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    loc.description,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 4.dp))

                                Text(
                                    "КЛИНИЧЕСКИЙ ОТЧЕТ ОБЫСКА:",
                                    color = RetroNeonCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                )
                                Text(
                                    loc.descriptionDetailed,
                                    color = Color.LightGray,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )

                                if (loc.cluesFound.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "СВЯЗАННЫЕ ВЕЩЕСТВЕННЫЕ УЛИКИ:",
                                        color = RetroOrange,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    loc.cluesFound.forEach { cId ->
                                        val cName = StaticGameData.CLUES.firstOrNull { it.id == cId }?.title ?: cId
                                        Text(
                                            "✔ Извлечено: ${cName.uppercase()}",
                                            color = RetroNeonGreen,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        )
                                    }
                                }

                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tv,
                                        contentDescription = null,
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "РАЙОН ДОСТУПА ЗАБЛОКИРОВАН",
                                        color = RetroAlertRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        "Просканируйте и сопоставьте соответствующие вещдоки и файлы в пульте Улик, чтобы разблокировать доступ к геолокации.",
                                        color = Color.Gray,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        if (isUnlocked && loc.id != "home") {
                            Spacer(modifier = Modifier.height(8.dp))
                            RetroButton(
                                onClick = { viewModel.travelToLocation(loc.id) },
                                buttonColor = RetroBgHeader,
                                borderColor = RetroNeonGreen,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ВЫСЛАТЬ НАРЯД ОПЕРАТИВНИКОВ", color = RetroNeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RetroBorderLine,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ВЫБЕРИТЕ ТОЧКУ НА РАДИУСНОЙ КАРТЕ СЛЕВА",
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
