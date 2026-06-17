package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Security
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
import com.example.game.GameViewModel
import com.example.game.MailItem
import com.example.game.StaticGameData
import com.example.ui.*

@Composable
fun MailApp(
    viewModel: GameViewModel,
    hexInput: String,
    hexDecryptedText: String
) {
    var selectedMail by remember { mutableStateOf<MailItem?>(null) }
    var showCryptTab by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: MAILBOX NAV LIST & TOGGLE TABS
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.42f)
                .background(Color(0xFF060910))
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
                    "ПОЧТОВЫЙ ДЕШИФРАТОР СВЯЗИ",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // High performance custom retro flat tabs
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .background(if (!showCryptTab) Color(0xFF101B2E) else RetroBgMedium)
                        .border(1.dp, if (!showCryptTab) RetroNeonGreen else RetroBorderLine)
                        .clickable { showCryptTab = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ВХОДЯЩИЕ",
                        color = if (!showCryptTab) RetroNeonGreen else Color.Gray,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(30.dp)
                        .background(if (showCryptTab) Color(0xFF261D10) else RetroBgMedium)
                        .border(1.dp, if (showCryptTab) RetroOrange else RetroBorderLine)
                        .clickable { showCryptTab = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = if (showCryptTab) RetroOrange else Color.Gray, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "ДЕКОДЕР HEX",
                            color = if (showCryptTab) RetroOrange else Color.Gray,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (!showCryptTab) {
                // List of Mails
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(StaticGameData.MAILS) { mail ->
                        val isSelected = selectedMail?.id == mail.id
                        val borderTint = if (isSelected) RetroNeonGreen else RetroBorderLine
                        val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgTint)
                                .border(1.dp, borderTint)
                                .clickable { selectedMail = mail }
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mail.sender.uppercase(),
                                        color = if (mail.sender.contains("Ghost")) RetroAlertRed else RetroNeonGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Icon(
                                        imageVector = if (mail.isEncrypted) Icons.Default.Security else Icons.Default.Email,
                                        contentDescription = null,
                                        tint = if (mail.isEncrypted) RetroOrange else Color.Gray,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mail.subject.uppercase(),
                                    color = Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            } else {
                // Info block about Cryptography in OSINT
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RetroBgMedium)
                        .border(1.dp, RetroBorderLine)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "СПРАВКА ДЕКОДЕРА:\n\nЧасто вещдоки содержат байты шестнадцатеричных переписок хакера Ghost_13.\n\nКаждый блок из двух цифр преобразуется в печатные символы латиницы/кириллицы UTF-8.\n\nВставьте полученный шестнадцатеричный буфер справа, чтобы дешифровать улику.",
                        color = Color.LightGray,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // RIGHT COL: SELECTED MAIL VIEW / CODES SOLVER
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!showCryptTab) {
                // NORMAL MAIL PREVIEW
                val mail = selectedMail
                if (mail != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            RetroCard(
                                borderColor = if (mail.sender.contains("Ghost")) RetroAlertRed else RetroNeonGreen,
                                backgroundColor = RetroBgMedium
                            ) {
                                Text(
                                    "ОТПРАВИТЕЛЬ: ${mail.sender.uppercase()}",
                                    color = if (mail.sender.contains("Ghost")) RetroAlertRed else RetroNeonGreen,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "ТЕМА: ${mail.subject}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                                )

                                Divider(color = RetroBorderLine, modifier = Modifier.padding(vertical = 4.dp))

                                Spacer(modifier = Modifier.height(6.dp))

                                if (mail.isEncrypted) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF261D10))
                                            .border(1.dp, RetroOrange)
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = RetroOrange, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("ШИФРОВАННЫЙ СЕКТОР [HEX]", color = RetroOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                mail.encryptedBody,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                lineHeight = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                "Указание: Скопируйте эту строку в HEX Декодер на левой вкладке, чтобы выявить настоящее имя подозреваемого.",
                                                color = Color.LightGray,
                                                fontSize = 8.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        mail.body,
                                        color = Color.LightGray,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 15.sp
                                    )
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
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = RetroBorderLine,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ОТКРОЙТЕ ПИСЬМО ВСПУТНИКОВОГО КАНАЛА СЛЕВА",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // INTERACTIVE CODED DECRYPTER PANEL
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "КРИПТОГРАФИЧЕСКИЙ ДЕШИФРАТОР СВЯЗИ РФ",
                        color = RetroOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { viewModel.setHexInput(it) },
                        label = { Text("Вставьте шестнадцатеричные байты", color = Color.Gray, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RetroOrange,
                            unfocusedBorderColor = RetroBorderLine,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = RetroOrange
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(0.dp) // Extremely sharp corners!
                    )

                    Text(
                        "РЕЗУЛЬТАТ ДЕКОДИРОВАНИЯ UTF-8:",
                        color = Color.Gray,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color(0xFF030509))
                            .border(1.dp, RetroOrange)
                            .padding(10.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = if (hexDecryptedText.isBlank()) "Ожидание ввода HEX байт..." else hexDecryptedText,
                            color = if (hexDecryptedText.contains("Kuzmin") || hexDecryptedText.contains("Кузьмин")) RetroNeonGreen else Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (hexDecryptedText.contains("Kuzmin")) {
                        RetroButton(
                            onClick = {
                                viewModel.zoomClue("encrypted_email")
                                viewModel.showMessage("Доказательство расшифровано! Ghost_13 это Kuzmin. Ключ добавлен на стенд улик!")
                            },
                            buttonColor = Color(0xFF102619),
                            borderColor = RetroNeonGreen,
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = RetroNeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ЗАРЕГИСТРИРОВАТЬ ДЕКОД ИМЕНИ КУЗМИН", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
