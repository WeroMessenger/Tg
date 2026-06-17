package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import com.example.ui.*

data class SocialProfile(
    val name: String,
    val username: String,
    val status: String,
    val posts: List<SocialPost>
)

data class SocialPost(
    val date: String,
    val text: String,
    val likes: Int
)

@Composable
fun SocialsApp(viewModel: GameViewModel) {
    var selectedProfileName by remember { mutableStateOf("Анна Лебедева") }

    val profiles = listOf(
        SocialProfile(
            name = "Анна Лебедева",
            username = "@anna_blogger_msc",
            status = "Ушла искать скрытые миры... Если завтра не в сети, ищите снимок завода Красный Октябрь в облаке!",
            posts = listOf(
                SocialPost("09 июня", "Ребята! Я копаю в верном направлении. Старые бункеры Москвы хранят много секретов. Какой-то Ghost_13 скинул мне загадочный архив. Иду на 'Красный Октябрь'... Сектор Б.", 24),
                SocialPost("06 июня", "Заброшенный завод — самое мистическое место в округе. Сделала селфи у главных ворот. Там странные гравировки. Ждите фото!", 15)
            )
        ),
        SocialProfile(
            name = "Дмитрий Крылов",
            username = "@dmitry_taxi_driver",
            status = "Офлайн (последний раз 12 июня в 02:45)",
            posts = listOf(
                SocialPost("12 июня", "Очередная ночная смена. Странные нынче клиенты. Какая-то бронь с виртуального номера через Даркнет, везем к ж/д мосту... Надеюсь, заплатят.", 4),
                SocialPost("10 июня", "Залил полный бак на АЗС у ж/д переезда. Пришлось сохранить бумажный чек — терминал заглючил и списал средства дважды. Аккуратнее!", 1)
            )
        ),
        SocialProfile(
            name = "Марина Соколова",
            username = "@marina_it_teacher",
            status = "Учу криптографии. Истину легче всего закодировать у всех на виду.",
            posts = listOf(
                SocialPost("14 июня", "Кто-то постоянно взламывает сервера нашей ИТ школы. Поток зашифрованных писем в HEX. Попробовала пробить админа форума 'Последний След'. Опасайтесь ников на Ghost_...", 41),
                SocialPost("12 июня", "Пробила логи входа в Метро... Моя карта Тройка сбоит на 'Станции Складская'. Почему все пути упорно сходятся к радиозаводу?", 18)
            )
        )
    )

    Row(modifier = Modifier.fillMaxSize().background(RetroBgDark)) {
        // Left Column: profiles list
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
                Box(modifier = Modifier.size(5.dp).background(RetroNeonCyan))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "НАБЛЮДАТЕЛЬ VKTALK",
                    color = RetroNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            profiles.forEach { profile ->
                val isSelected = selectedProfileName == profile.name
                val borderTint = if (isSelected) RetroNeonGreen else RetroBorderLine
                val bgTint = if (isSelected) Color(0xFF101B2E) else RetroBgMedium

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(bgTint)
                        .border(1.dp, borderTint)
                        .clickable { selectedProfileName = profile.name }
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = profile.name,
                            tint = if (isSelected) RetroNeonGreen else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = profile.name.uppercase(),
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                profile.username,
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Right Column: Wall & Posts feed
        val activeProfile = profiles.firstOrNull { it.name == selectedProfileName }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(RetroBgDark)
                .padding(10.dp)
        ) {
            if (activeProfile != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        // Profile header Card
                        RetroCard(
                            borderColor = RetroNeonCyan,
                            backgroundColor = RetroBgMedium
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = RetroNeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "АККАУНТ ИСТОЧНИКА • СРЕДА ИНТЕРПОЛЯЦИИ",
                                    color = RetroNeonCyan,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                activeProfile.name.uppercase(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "СТАТУС: \"${activeProfile.status}\"",
                                color = Color.LightGray,
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 13.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "СКРАПЕР ПОСТОВ НА СТЕНЕ:",
                            color = Color.Gray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(activeProfile.posts) { post ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RetroBgMedium)
                                .border(1.dp, RetroBorderLine)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = post.date.uppercase(),
                                        color = RetroAlertRed,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "${post.likes}",
                                            color = Color.Gray,
                                            fontSize = 7.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.text,
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
        }
    }
}
