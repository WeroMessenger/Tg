package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
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
import com.example.game.DbRecord
import com.example.game.GameViewModel
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun DatabaseApp(
    viewModel: GameViewModel,
    searchQuery: String
) {
    var activeRecord by remember { mutableStateOf<DbRecord?>(null) }

    val filteredRecords = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            StaticGameData.DB_RECORDS
        } else {
            StaticGameData.DB_RECORDS.filter {
                it.queryKey.contains(searchQuery, ignoreCase = true) ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.summary.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: ACC USE CASE QUERY SEARCH PANEL
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.48f)
                .background(Color(0xFF070B14))
                .border(1.dp, RetroBorderLine)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.size(5.dp).background(RetroNeonGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "ПОИСКОВЫЙ СЕРВЕР МВД РФ [СЕКТОР-OSINT]",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // High priority custom sharp terminal search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setDbSearchQuery(it) },
                label = { Text("Введите ФИО, Дело или Ключ", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = RetroNeonGreen, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RetroNeonGreen,
                    unfocusedBorderColor = RetroBorderLine,
                    focusedLabelColor = RetroNeonGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(0.dp), // Extremely sharp corners!
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Results List in custom sharp listing blocks
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredRecords) { rec ->
                    val isSelected = activeRecord?.queryKey == rec.queryKey
                    val borderTint = if (isSelected) RetroNeonGreen else RetroBorderLine
                    val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgTint)
                            .border(1.dp, borderTint)
                            .clickable { activeRecord = rec }
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rec.category.uppercase(),
                                    color = RetroAlertRed,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "[KEY: ${rec.queryKey.uppercase()}]",
                                    color = RetroNeonCyan,
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = rec.title.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                            Text(
                                text = rec.summary,
                                color = Color.LightGray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // RIGHT COL: MAIN DETAILED DOSSIER DATABASE SHEET
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val record = activeRecord
            if (record != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        RetroCard(
                            borderColor = RetroNeonGreen,
                            backgroundColor = RetroBgMedium,
                            glow = true
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = RetroNeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "АРХИВ МВД • СЕКРЕТНЫЙ РАЗДЕЛ",
                                    color = RetroNeonGreen,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 10.dp))

                            Text(
                                record.title.uppercase(),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "КАТЕГОРИЯ СИСТЕМЫ: ${record.category.uppercase()}",
                                color = RetroAlertRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 1.dp, bottom = 8.dp)
                            )

                            Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                "ОПЕРАТИВНОЕ ОПИСАНИЕ И ЛОГИ ИЗЪЯТИЯ:",
                                color = Color.Gray,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            Text(
                                record.detailedText,
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = RetroBorderLine,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ВВЕДИТЕ ЗАПРОС СЛЕВА ДЛЯ НАЧАЛА ПОИСКА",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = Alignment.CenterHorizontally.let { TextAlign.Center }
                    )
                }
            }
        }
    }
}
