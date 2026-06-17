package com.example.game

data class ClueItem(
    val id: String,
    val title: String,
    val description: String,
    val type: ClueType,
    val summary: String, // Short summary shown on the board
    val details: String, // Extended text shown when magnified/analyzed
    val isRevealedByDefault: Boolean = false,
    val imagePath: String = "", // Used for image analysis
    val defaultX: Float = 0f,
    val defaultY: Float = 0f
)

enum class ClueType {
    DOSSIER, TEXT, IMAGE, ENCRYPTED, FILE
}

data class MapLocation(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val unlockedByClue: String = "", // Clue connection pattern required to unlock
    val descriptionDetailed: String,
    val cluesFound: List<String> = emptyList() // Clue IDs unlocked when visiting this location
)

data class DbRecord(
    val queryKey: String,
    val title: String,
    val category: String,
    val summary: String,
    val detailedText: String
)

data class MailItem(
    val id: String,
    val sender: String,
    val subject: String,
    val body: String,
    val isEncrypted: Boolean = false,
    val encryptedBody: String = "",
    val decryptionKey: String = "",
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val isPlayer: Boolean,
    val timestamp: String,
    val options: List<ChatOption> = emptyList()
)

data class ChatOption(
    val text: String,
    val xpReward: Int,
    val nextMessageId: String,
    val triggersClue: String = "" // if selecting this option unlocks a clue
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String // "OSINT", "Deduction", "Story"
)

object StaticGameData {
    val CLUES = listOf(
        // CHAPTER 1 - Anna Lebedeva
        ClueItem(
            id = "anna_profile",
            title = "Досье: Анна Лебедева",
            description = "Анна Сергеевна Лебедева, 21 год. Студентка-журналистка. Пропала неделю назад.",
            type = ClueType.DOSSIER,
            summary = "21 год, студентка. Расследовала заброшки города.",
            details = "Студентка факультета журналистики МГУ. Вела блог об институализированных подземельях и секретах города. В своем блоге писала о сталкерской группе, связанной с Ghost_13. Последний раз выходила на связь 10 июня.",
            isRevealedByDefault = true,
            defaultX = 100f,
            defaultY = 150f
        ),
        ClueItem(
            id = "anna_note",
            title = "Фрагмент переписки Анны",
            description = "Переписка Анны с подругой Машей из ВКонтакте, изъятая с её ноутбука.",
            type = ClueType.TEXT,
            summary = "Кажется, я нашла что-то важное... У старого завода.",
            details = "Чат от 9 июня:\nАнна: «Маш, кажется, я нашла что-то действительно важное. Источник слил координаты их логова. Иду проверять у старого завода. Если не вернусь завтра, проверь фотографии!»\nМаша: «Ань, не глупи, это опасно!»",
            isRevealedByDefault = false,
            defaultX = 350f,
            defaultY = 150f
        ),
        ClueItem(
            id = "factory_photo",
            title = "Фотография у завода",
            description = "Последняя фотография, загруженная в её облако за пару часов до пропажи.",
            type = ClueType.IMAGE,
            summary = "Снимок старых ворот. Внизу виден едва заметный номер на кирпиче.",
            details = "При глубоком анализе (зуме) под вывеской «ОПАСНАЯ ЗОНА» можно разглядеть вырезанное число 55733 и нарисованную стрелку влево. Это могут быть координаты широты (55.733)! На переднем плане — старая цистерна с маркировкой 'СЕКТОР_Б 37601' (долгота 37.601!).",
            imagePath = "factory_clue_image",
            isRevealedByDefault = false,
            defaultX = 600f,
            defaultY = 150f
        ),
        ClueItem(
            id = "factory_coords",
            title = "Координаты завода",
            description = "Точные географические координаты, полученные при анализе метаданных и скрытых деталей фото.",
            type = ClueType.FILE,
            summary = "Широта: 55.733, Долгота: 37.601 (Красный Октябрь)",
            details = "Координаты указывают на заброшенный заводской комплекс «Красный Октябрь» в промышленной зоне. На этом заводе находится Сектор Б, упомянутый в заметке. Нам нужно прибыть туда через карту города.",
            isRevealedByDefault = false,
            defaultX = 850f,
            defaultY = 150f
        ),

        // CHAPTER 2 - Dmitry Krylov
        ClueItem(
            id = "dmitry_profile",
            title = "Досье: Дмитрий Крылов",
            description = "Дмитрий Олегович Крылов, 34 года. Водитель такси. Пропал 4 дня назад.",
            type = ClueType.DOSSIER,
            summary = "34 года, таксист. Машина брошена на путях.",
            details = "Водитель службы 'СитиТранс'. Пропал в ночь с 12 на 13 июня. Его автомобиль Hyundai Solaris был найден с открытыми дверями и работающим двигателем недалеко от железнодорожного переезда.",
            isRevealedByDefault = true,
            defaultX = 100f,
            defaultY = 400f
        ),
        ClueItem(
            id = "taxi_trip",
            title = "Лог поездок такси",
            description = "История заказов из личного кабинета водителя за последнюю смену.",
            type = ClueType.FILE,
            summary = "Финальный заказ от анонимного +79998881313.",
            details = "Заказ №4399:\nВремя: 02:15 - 02:40\nОткуда: ул. Мира, 12\nКуда: Железнодорожный переезд 14-й км\nЗаказчик: скрытый номер (в базе бьется как виртуальная SIM-карта +79998881313, зарегистрированная на Ghost_13).",
            isRevealedByDefault = false,
            defaultX = 350f,
            defaultY = 400f
        ),
        ClueItem(
            id = "gas_receipt",
            title = "Чек с заправки",
            description = "Чек, обнаруженный оперативниками в подстаканнике брошенного автомобиля.",
            type = ClueType.TEXT,
            summary = "Заправка АЗС-Нефть под ж/д мостом, время: 02:28.",
            details = "Оплата картой Дмитрия Крылова на АЗС №9 (находится рядом с заброшенным переездом и станцией). Время: 02:28. ID терминала: T-9082. Это подтверждает, что машина была на месте около 2:30 ночи.",
            isRevealedByDefault = false,
            defaultX = 600f,
            defaultY = 400f
        ),
        ClueItem(
            id = "camera_station",
            title = "Камера №4: Ж/Д Станция",
            description = "Запись с дорожного регистратора у станции.",
            type = ClueType.IMAGE,
            summary = "Тень подозрительного мужчины в плаще, уводящего Дмитрия.",
            details = "Кадр от 02:46 ночи. Из машины такси выходит Дмитрий Крылов под прицелом человека высокого роста в черной куртке. На куртке эмблема ветеранов органов МВД. Они направляются через пути к Ж/Д станции.",
            imagePath = "station_clue_image",
            isRevealedByDefault = false,
            defaultX = 850f,
            defaultY = 400f
        ),

        // CHAPTER 3 - Marina Sokolova
        ClueItem(
            id = "marina_profile",
            title = "Досье: Марина Соколова",
            description = "Марина Игоревна Соколова, 28 лет. Учитель информатики. Пропала 2 дня назад.",
            type = ClueType.DOSSIER,
            summary = "28 лет, учитель. Получала шифр-письма.",
            details = "Преподаватель информационных технологий в Лицее №5. Увлекалась криптографией и форензикой. Подозревала, что кто-то следит за её школьной сетью.",
            isRevealedByDefault = true,
            defaultX = 100f,
            defaultY = 650f
        ),
        ClueItem(
            id = "encrypted_email",
            title = "Шифрованное письмо",
            description = "Электронное письмо с заголовком [CONFIDENTIAL], пришедшее на её рабочую почту.",
            type = ClueType.ENCRYPTED,
            summary = "Загадочный шестнадцатеричный код (HEX).",
            details = "Текст письма:\n«0x47 0x68 0x6f 0x73 0x74 0x5f 0x31 0x33 0x20 0x69 0x73 0x20 0x4b 0x75 0x7a 0x6d 0x69 0x6e»\n\nПри расшифровке HEX в текст получается:\n«Ghost_13 is Kuzmin»",
            imagePath = "hex_email_image",
            isRevealedByDefault = false,
            defaultX = 350f,
            defaultY = 650f
        ),
        ClueItem(
            id = "browser_history",
            title = "История браузера Марины",
            description = "Выписка запросов из Google-аккаунта Марины, предоставленная хостинг-провайдером.",
            type = ClueType.TEXT,
            summary = "Поиск Кузьмина в архивах МВД.",
            details = "Запросы от 14 июня:\n" +
                    "15:10 - «Андрей Кузьмин капитан полиции увольнение»\n" +
                    "15:24 - «Ghost_13 форум хакеров Склад Октябрьский»\n" +
                    "15:40 - «как расшифровать hex в utf8 онлайн»",
            isRevealedByDefault = false,
            defaultX = 600f,
            defaultY = 650f
        ),
        ClueItem(
            id = "subway_log",
            title = "Данные карты метрополитена",
            description = "Лог поездок по карте 'Тройка' Марины Соколовой.",
            type = ClueType.FILE,
            summary = "Поездка до станции метро Складская в 18:22.",
            details = "Карта зафиксирована:\n18:02 - Вход на станцию 'Парк Культуры'.\n18:22 - Выход на станции 'Складская промзона'. Это конечная точка её пути. Рядом находится Заброшенный Склад №5.",
            isRevealedByDefault = false,
            defaultX = 850f,
            defaultY = 650f
        )
    )

    val MAP_LOCATIONS = listOf(
        MapLocation(
            id = "home",
            name = "Полицейский Участок",
            type = "Офис",
            description = "Твое рабочее место. Здесь безопасно и есть доступ к базам данных.",
            lat = 55.755,
            lng = 37.617,
            descriptionDetailed = "Главное управление полиции. На Вашем столе горит лампа, кружка остывшего кофе и подключение к закрытой базе данных МВД.",
            cluesFound = emptyList()
        ),
        MapLocation(
            id = "factory",
            name = "Завод 'Красный Октябрь'",
            type = "Заброшка",
            description = "Заброшенный индустриальный гигант на окраине города.",
            lat = 55.733,
            lng = 37.601,
            unlockedByClue = "factory_coords",
            descriptionDetailed = "Полуразрушенный кирпичный цех. Внутри Вы находите рюкзак Анны Лебедевой и её записку (улика 'Фрагмент переписки Анны' разблокирована!). Также на стене выцарапан символ 'G-13'. Вы на верном пути.",
            cluesFound = listOf("anna_note", "factory_photo")
        ),
        MapLocation(
            id = "railway",
            name = "Железнодорожная Станция",
            type = "Транспорт",
            description = "Тихий переезд и старая платформа. Здесь нашли такси Дмитрия.",
            lat = 55.761,
            lng = 37.585,
            unlockedByClue = "camera_station",
            descriptionDetailed = "На путях стоит пустая машина. В бардачке вы находите телефон с логом поездок (разблокирована улика 'Лог поездок такси'!), а в подстаканнике — чек с заправки (разблокирована улика 'Чек с заправки'!). На камере охраны неподалеку заметен похититель.",
            cluesFound = listOf("taxi_trip", "gas_receipt")
        ),
        MapLocation(
            id = "subway",
            name = "Станция 'Складская'",
            type = "Метро",
            description = "Малолюдная конечная станция метро у промышленной зоны.",
            lat = 55.712,
            lng = 37.654,
            unlockedByClue = "subway_log",
            descriptionDetailed = "Камеры турникетов подтверждают: Марина Соколова вышла здесь и направилась к заброшенным складам электроники.",
            cluesFound = emptyList()
        ),
        MapLocation(
            id = "warehouse",
            name = "Заброшенный Склад №5",
            type = "Укрытие",
            description = "Склад электроники, обанкротившийся в 2024 году.",
            lat = 55.708,
            lng = 37.662,
            unlockedByClue = "subway_log",
            descriptionDetailed = "Анализируя перекрестки, вы выходите на этот склад. Здесь Марина пряталась или была схвачена. Вы находите разорванное письмо (улика 'Шифрованное письмо' разблокирована!) и её брошенный телефон с историей поиска (улика 'История браузера Марины' разблокирована!).",
            cluesFound = listOf("encrypted_email", "browser_history", "subway_log", "camera_station")
        )
    )

    val DB_RECORDS = listOf(
        DbRecord(
            queryKey = "VORONOV",
            title = "Воронов Алексей (Вы)",
            category = "МВД РФ",
            summary = "Детектив-стажер, занимающийся поиском пропавших.",
            detailedText = "Младший лейтенант полиции Воронов А. С. Назначен в отдел розыска 3 месяца назад. Нынешнее дело — его последний шанс. Приказ руководства: 'Раскрыть в течение недели или рапорт на стол'."
        ),
        DbRecord(
            queryKey = "LEBEDEVA",
            title = "Лебедева Анна Сергеевна",
            category = "Пропавшие",
            summary = "Студент-журналист. Пропала 10 июня.",
            detailedText = "Статус: Розыск. Дочь профессора Лебедева. Телефон отключен с вечера 10 июня. К делу приложен список её контактов в соцсети, где фигурирует подозрительный аккаунт Ghost_13."
        ),
        DbRecord(
            queryKey = "KRYLOV",
            title = "Крылов Дмитрий Олегович",
            category = "Пропавшие",
            summary = "Таксист. Пропал в ночь на 13 июня.",
            detailedText = "Статус: Розыск. Ранее не судим. Семья характеризует его как спокойного человека. Странность: перед исчезновением заправлялся на окраине и совершал незарегистрированный рейс по звонку."
        ),
        DbRecord(
            queryKey = "SOKOLOVA",
            title = "Соколова Марина Игоревна",
            category = "Пропавшие",
            summary = "Преподаватель IT. Пропала 15 июня.",
            detailedText = "Статус: Розыск. Специалист по сетевой безопасности, помогала настраивать базы данных Лицея. Из архива переписок извлечены электронные письма в формате HEX-байтов."
        ),
        DbRecord(
            queryKey = "KUZMIN",
            title = "Кузьмин Андрей Викторович",
            category = "Уволенные сотрудники",
            summary = "Капитан полиции. Уволен со скандалом в 2024 году.",
            detailedText = "Капитан полиции Кузьмин А. В., бывший начальник ИТ-отдела розыска. Уволен за несанкционированный доступ к секретным архивам. Известен как высококлассный специалист по компьютерной безопасности и OSINT. Имеет позывной 'Призрак' (Ghost). Мотив: доказать превосходство OSINT над традиционными методами МВД, обвинял систему в лени и закрытии дел за неимением улик."
        ),
        DbRecord(
            queryKey = "GHOST_13",
            title = "Ghost_13",
            category = "Хакеры / Подозреваемые",
            summary = "Анонимный куратор квеста в Даркнете.",
            detailedText = "Анонимная личность в сети. Администрирует форум 'Последний След'. Заставлял жертв разгадывать загадки. Тесно связан с делом Кузьмина. Использует почтовый ящик ghost_13@darknet.io."
        )
    )

    val MAILS = listOf(
        MailItem(
            id = "boss_welcome",
            sender = "Дмитрий Петрович (Начальник)",
            subject = "ТВОЕ ПОСЛЕДНЕЕ ДЕЛО",
            body = "Алексей, у тебя ровно неделя. В городе пропало уже 12 человек, Общественность бьет тревогу, а наше министерство рвет и мечет. Предыдущие следователи ничего не нашли и закрыли дела. Если завалишь и это — вылетишь со свистом. Начни с досье Анны Лебедевой в базе. Удачи.\n\nP.S. Подсказки будут зависеть от сложности в твоем профиле.",
            isRead = false
        ),
        MailItem(
            id = "ghost_riddle_1",
            sender = "ghost_13@darknet.io",
            subject = "ПРАВИЛА ИГРЫ",
            body = "Приветствую, детектив Воронов. Ты думаешь, что твоя бумажная бюрократия спасет этих людей? Давай сыграем в OSINT-игру. Каждая жертва оставила след. Если ты проявишь интеллект и свяжешь правильные улики на своей маркерной доске — они выживут. Ошибешься — они станут лишь призраками. Первая зацепка спрятана на фотографии у завода в Секторе Б. Найди её координаты.",
            isRead = false
        ),
        MailItem(
            id = "marina_secret",
            sender = "Соколова Марина (через форвард)",
            subject = "[ВАЖНО] Моё расследование G-13",
            body = "Кто бы это ни читал... Я взломала один из серверов Ghost_13. Он бывший мент! В базе его фамилия зашифрована в шестнадцатеричном виде. Код: 0x47(G) 0x68(h) 0x6f(o) 0x73(s) 0x74(t) 0x5f( _ ) 0x31(1) 0x33(3) 0x20( ) 0x69(i) 0x73(s) 0x20( ) 0x4b(K) 0x75(u) 0x7a(z) 0x6d(m) 0x69(i) 0x6e(n)... Да, Ghost_13 это Кузьмин! Боже, он следит за мной...",
            isEncrypted = true,
            encryptedBody = "47686F73745F3133206973204B757A6D696E",
            decryptionKey = "HEX",
            isRead = false
        )
    )

    val ACHIEVEMENTS = listOf(
        AchievementItem("first_clue", "Первая улика", "Свяжите первую правильную пару улик на доске расследования.", "Deduction"),
        AchievementItem("analyst", "Аналитик", "Проанализируйте скрытые координаты на фотографии старого завода.", "OSINT"),
        AchievementItem("sleuth", "Следователь", "Откройте все 5 ключевых локаций на карте города.", "Story"),
        AchievementItem("master", "Мастер OSINT", "Расшифруйте тайное шестнадцатеричное послание Марины без автоподсказок.", "OSINT"),
        AchievementItem("case_closed", "Дело раскрыто!", "Раскройте истинную личность Ghost_13 и соберите полный пазл.", "Story")
    )
}
