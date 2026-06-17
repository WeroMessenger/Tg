package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
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
import com.example.game.ChatMessage
import com.example.game.ChatOption
import com.example.game.GameViewModel
import com.example.ui.*

@Composable
fun ChatApp(
    viewModel: GameViewModel,
    chatHistoryState: String // Comma-separated keys, e.g. "boss:nextId", etc.
) {
    val historyKeys = remember(chatHistoryState) {
        chatHistoryState.split(",").filter { it.isNotEmpty() }.toSet()
    }

    var activeChatId by remember { mutableStateOf("boss") }

    // Dynamic dialogue generator
    val bossMessages = mutableListOf(
        ChatMessage("boss_init_1", "Дмитрий Петрович", "Воронов, ты продвигаешься по Анне Лебедевой? Где зацепки?", false, "09:00")
    )
    if (historyKeys.contains("boss:boss_ans_1")) {
        bossMessages.add(ChatMessage("boss_ans_1", "Вы", "Да, изучаю её досье. Выяснил контакты.", true, "09:02"))
        bossMessages.add(ChatMessage("boss_init_2", "Дмитрий Петрович", "Прекрасно. Её подруга Маша на связи в чате. Спроси у неё про фотографии заброшенного завода!", false, "09:03"))
    }
    if (historyKeys.contains("boss:boss_ans_2")) {
        bossMessages.add(ChatMessage("boss_ans_2", "Вы", "Она упоминала старый завод. Я ищу его точное местоположение.", true, "09:05"))
        bossMessages.add(ChatMessage("boss_init_3", "Дмитрий Петрович", "Завод Красный Октябрь? Говорят, Анна оставила координаты в облаке. Сделай спектральный анализ её снимков в Галерее!", false, "09:06"))
    }
    if (historyKeys.contains("boss:boss_ans_3")) {
        bossMessages.add(ChatMessage("boss_ans_3", "Вы", "Я выявил координаты ворот завода: 55.733, 37.601.", true, "10:15"))
        bossMessages.add(ChatMessage("boss_init_4", "Дмитрий Петрович", "Молодец, лейтенант! Локация 'Завод' открыта на карте. Выезжай для осмотра места преступления!", false, "10:16"))
    }

    val mashaMessages = mutableListOf(
        ChatMessage("masha_init_1", "Маша (Подруга)", "Привет! Вы ведь следователь, да? Вы найдете Анечку? Прошу вас...", false, "10:00")
    )
    if (historyKeys.contains("masha:masha_ans_1")) {
        mashaMessages.add(ChatMessage("masha_ans_1", "Вы", "Привет, Маша. Да, делаем всё возможное. Что она тебе рассказывала?", true, "10:02"))
        mashaMessages.add(ChatMessage("masha_init_2", "Маша (Подруга)", "Она увлекалась диггерством. Кажется, нашла логово хакера Ghost_13. Проверь её фотки ворот завода в Галерее!", false, "10:03"))
    }

    val krotMessages = mutableListOf(
        ChatMessage("krot_init_1", "Крот (Даркнет)", "Здорово, опер. Ищешь Марину Соколову? Забудь свои совковые архивы. Тут нужен OSINT.", false, "12:00")
    )
    if (historyKeys.contains("krot:krot_ans_1")) {
        krotMessages.add(ChatMessage("krot_ans_1", "Вы", "Что у тебя есть на Марину?", true, "12:02"))
        krotMessages.add(ChatMessage("krot_init_2", "Крот (Даркнет)", "Она хакнула сервак Ghost_13 и со всеми поделилась письмом с HEX шифром. Используй HEX Декодер, чтобы перевести строку!", false, "12:03"))
    }

    val currentMessages = when (activeChatId) {
        "boss" -> bossMessages
        "masha" -> mashaMessages
        "krot" -> krotMessages
        else -> emptyList()
    }

    // Determine currently available dialogue choices
    val activeOptions = when (activeChatId) {
        "boss" -> {
            when {
                !historyKeys.contains("boss:boss_ans_1") -> listOf(
                     ChatOption("Да, изучаю её досье. Выяснил контакты.", 15, "boss_ans_1", "anna_profile")
                )
                historyKeys.contains("boss:boss_ans_1") && !historyKeys.contains("boss:boss_ans_2") -> listOf(
                     ChatOption("Она упоминала старый завод. Ищу его на карте.", 20, "boss_ans_2", "factory_photo")
                )
                historyKeys.contains("boss:boss_ans_2") && !historyKeys.contains("boss:boss_ans_3") -> listOf(
                     ChatOption("Нашел координаты завода: (55.733, 37.601).", 30, "boss_ans_3", "factory_coords")
                )
                else -> emptyList()
            }
        }
        "masha" -> {
             when {
                 !historyKeys.contains("masha:masha_ans_1") -> listOf(
                     ChatOption("Привет, Маша. Расскажи про увлечения Анны.", 20, "masha_ans_1", "factory_photo")
                 )
                 else -> emptyList()
             }
        }
        "krot" -> {
             when {
                 !historyKeys.contains("krot:krot_ans_1") -> listOf(
                     ChatOption("Что тебе известно про её зашифрованные логи?", 20, "krot_ans_1", "encrypted_email")
                 )
                 else -> emptyList()
             }
        }
        else -> emptyList()
    }

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // LEFT COL: DISPATCHED CONTACTS
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.38f)
                .background(Color(0xFF070B14))
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
                    "ГОРЯЧАЯ СВЯЗЬ МВД РФ",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            val contacts = listOf(
                Triple("boss", "Дмитрий Петрович", "Начальник ОУР"),
                Triple("masha", "Маша (Свидетель)", "Подруга пропавшей"),
                Triple("krot", "Крот (Даркнет)", "Информатор")
            )

            contacts.forEach { (cId, name, role) ->
                val isSelected = activeChatId == cId
                val borderTint = if (isSelected) RetroNeonGreen else RetroBorderLine
                val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(bgTint)
                        .border(1.dp, borderTint)
                        .clickable { activeChatId = cId }
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = name,
                            tint = if (isSelected) RetroNeonGreen else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = name.uppercase(),
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                role,
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // RIGHT COL: MESSENGER DIALOGUE VIEWPORT
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(10.dp)
        ) {
            // Messages content area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(currentMessages) { msg ->
                    val alignment = if (msg.isPlayer) Alignment.CenterEnd else Alignment.CenterStart
                    val bubbleColor = if (msg.isPlayer) Color(0xFF102619) else Color(0xFF161111)
                    val outlineColor = if (msg.isPlayer) RetroNeonGreen else RetroAlertRed

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        contentAlignment = alignment
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(bubbleColor)
                                .border(1.dp, outlineColor)
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg.sender.uppercase(),
                                        color = if (msg.isPlayer) RetroNeonGreen else RetroAlertRed,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = msg.timestamp,
                                        color = Color.Gray,
                                        fontSize = 7.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Choice Input Option bubble picker
            if (activeOptions.isNotEmpty()) {
                Text(
                    "РЕШЕНИЕ ЛЕЙТЕНАНТА СЛЕДСТВИЯ:",
                    color = RetroNeonCyan,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                activeOptions.forEach { opt ->
                    RetroButton(
                        onClick = {
                            viewModel.selectOption(
                                chatId = activeChatId,
                                sender = "Вы",
                                optionText = opt.text,
                                xpReward = opt.xpReward,
                                nextId = opt.nextMessageId,
                                triggersClue = opt.triggersClue
                            )
                        },
                        buttonColor = Color(0xFF0D1B24),
                        borderColor = RetroNeonCyan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt.text,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = RetroNeonCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "+${opt.xpReward} XP",
                                    color = RetroNeonCyan,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1216))
                        .border(1.dp, RetroBorderLine)
                        .padding(8.dp)
                ) {
                    Text(
                        "КАНАЛ ОЖИДАНИЯ: Собирайте улики или выполняйте связи, чтобы открыть новые диалоги.",
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
