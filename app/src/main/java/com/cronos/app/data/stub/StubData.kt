package com.cronos.app.data.stub

data class StubEvent(
    val id: String,
    val title: String,
    val date: String,
    val direction: String,
    val format: String,
    val points: Int,
    val difficulty: Int,
    val description: String = "",
    val organizerName: String = "Организатор",
    val reward: String = "Баллы",
    val maxParticipants: Int = 50,
    val currentParticipants: Int = 0
)

data class StubParticipant(
    val id: String,
    val name: String,
    val username: String,
    val city: String,
    val direction: String,
    val eventsCount: Int,
    val rating: Int,
    val level: String,
    val age: Int = 22,
    val achievements: List<Pair<String, Int>> = emptyList(),
    val forecast: String = ""
)

data class StubOrganizer(
    val id: String,
    val name: String,
    val organization: String,
    val eventsCount: Int,
    val avgRating: Float,
    val trustScore: Float,
    val typicalRewards: List<String>
)

val STUB_EVENTS = listOf(
    StubEvent("1", "Хакатон по AI", "2026-04-10", "IT", "offline", 150, 4, "Разработка AI-решений за 48 часов", "Иванов А.", "150 баллов + сертификат", 40, 28),
    StubEvent("2", "Кейс-чемпионат", "2026-05-01", "IT", "offline", 200, 5, "Решение реальных бизнес-кейсов", "Петров В.", "200 баллов + стажировка", 30, 22),
    StubEvent("3", "Медиашкола", "2026-04-15", "Медиа", "online", 80, 2, "Обучение основам журналистики", "Сидорова М.", "80 баллов", 100, 67),
    StubEvent("4", "Вебинар по SMM", "2026-05-05", "Медиа", "online", 50, 1, "Продвижение в социальных сетях", "Козлов Д.", "50 баллов", 200, 145),
    StubEvent("5", "Форум молодёжи", "2026-04-20", "Социальные проекты", "offline", 100, 3, "Обсуждение социальных инициатив", "Новикова А.", "100 баллов + мерч", 80, 54),
    StubEvent("6", "Волонтёрский марафон", "2026-04-25", "Социальные проекты", "offline", 90, 2, "Помощь городским проектам", "Морозов К.", "90 баллов", 60, 41),
    StubEvent("7", "Дебаты", "2026-05-10", "Политика", "offline", 120, 3, "Публичные дебаты по актуальным темам", "Волков С.", "120 баллов", 40, 18),
    StubEvent("8", "Молодёжный парламент", "2026-05-15", "Политика", "offline", 180, 4, "Симуляция парламентских заседаний", "Лебедев Р.", "180 баллов + приглашение на форум", 25, 12),
    StubEvent("9", "Научная конференция", "2026-05-20", "Наука", "online", 130, 4, "Презентация научных работ", "Соколов П.", "130 баллов", 50, 33),
    StubEvent("10", "Спортивный марафон", "2026-06-01", "Спорт", "offline", 70, 2, "Городской забег", "Попов Н.", "70 баллов + медаль", 300, 210),
    StubEvent("11", "Арт-фестиваль", "2026-06-10", "Культура", "offline", 60, 1, "Выставка молодых художников", "Зайцева О.", "60 баллов", 150, 89),
    StubEvent("12", "Стартап-питч", "2026-06-15", "Бизнес", "offline", 160, 4, "Презентация стартапов инвесторам", "Орлов Т.", "160 баллов + менторство", 20, 15),
)

val STUB_PARTICIPANTS = listOf(
    StubParticipant(
        "1", "Алексей Иванов", "alex_iv", "Москва", "IT", 15, 1250, "Reserve", 24,
        achievements = listOf("Хакатон по AI" to 150, "Стартап-питч" to 160, "Научная конференция" to 130, "Кейс-чемпионат" to 200),
        forecast = "Стабильно в резерве. Рекомендован для федерального форума."
    ),
    StubParticipant(
        "2", "Мария Петрова", "masha_p", "Казань", "Медиа", 12, 980, "Gold", 22,
        achievements = listOf("Медиашкола" to 80, "Вебинар по SMM" to 50, "Арт-фестиваль" to 60, "Форум молодёжи" to 100),
        forecast = "До Reserve осталось 270 баллов. Активна в медиа-направлении."
    ),
    StubParticipant(
        "3", "Дмитрий Сидоров", "dima_s", "Екатеринбург", "IT", 18, 1480, "Reserve", 25,
        achievements = listOf("Хакатон по AI" to 150, "Кейс-чемпионат" to 200, "Стартап-питч" to 160, "Научная конференция" to 130, "Вебинар по SMM" to 50),
        forecast = "Лидер направления IT. Кандидат на включение в федеральный резерв."
    ),
    StubParticipant(
        "4", "Анна Козлова", "anna_k", "Санкт-Петербург", "Социальные проекты", 9, 720, "Silver", 21,
        achievements = listOf("Форум молодёжи" to 100, "Волонтёрский марафон" to 90, "Арт-фестиваль" to 60),
        forecast = "До Gold осталось 280 баллов. Высокая активность в социальных проектах."
    ),
    StubParticipant(
        "5", "Иван Новиков", "ivan_n", "Новосибирск", "Политика", 20, 1650, "Reserve", 26,
        achievements = listOf("Молодёжный парламент" to 180, "Дебаты" to 120, "Форум молодёжи" to 100, "Кейс-чемпионат" to 200, "Хакатон по AI" to 150),
        forecast = "Топ-1 платформы. Рекомендован для включения в региональный кадровый резерв."
    ),
    StubParticipant(
        "6", "Ольга Морозова", "olga_m", "Краснодар", "Медиа", 7, 540, "Silver", 20,
        achievements = listOf("Медиашкола" to 80, "Вебинар по SMM" to 50, "Арт-фестиваль" to 60),
        forecast = "До Gold осталось 460 баллов. Рекомендуется участие в крупных медиа-событиях."
    ),
    StubParticipant(
        "7", "Сергей Волков", "sergey_v", "Воронеж", "IT", 11, 870, "Gold", 23,
        achievements = listOf("Хакатон по AI" to 150, "Стартап-питч" to 160, "Научная конференция" to 130),
        forecast = "До Reserve осталось 380 баллов. Стабильный рост в IT-направлении."
    ),
    StubParticipant(
        "8", "Елена Лебедева", "elena_l", "Ростов-на-Дону", "Наука", 6, 430, "Bronze", 19,
        achievements = listOf("Научная конференция" to 130, "Форум молодёжи" to 100),
        forecast = "До Silver осталось 70 баллов. Перспективный участник в научном направлении."
    ),
    StubParticipant(
        "9", "Павел Соколов", "pavel_s", "Тюмень", "Бизнес", 14, 1100, "Gold", 27,
        achievements = listOf("Стартап-питч" to 160, "Кейс-чемпионат" to 200, "Форум молодёжи" to 100, "Спортивный марафон" to 70),
        forecast = "До Reserve осталось 150 баллов. Высокий потенциал в бизнес-направлении."
    ),
    StubParticipant(
        "10", "Наталья Попова", "natasha_p", "Уфа", "Культура", 8, 610, "Silver", 22,
        achievements = listOf("Арт-фестиваль" to 60, "Медиашкола" to 80, "Форум молодёжи" to 100, "Волонтёрский марафон" to 90),
        forecast = "До Gold осталось 390 баллов. Активна в культурных и социальных мероприятиях."
    ),
)

val STUB_ORGANIZERS = listOf(
    StubOrganizer("1", "Иванов Алексей", "Молодёжный центр", 24, 4.8f, 0.92f, listOf("Стажировки", "Сертификаты", "Баллы")),
    StubOrganizer("2", "Петрова Мария", "IT-Хаб", 18, 4.6f, 0.87f, listOf("Баллы", "Мерч", "Приглашения на форумы")),
    StubOrganizer("3", "Сидоров Дмитрий", "Медиашкола", 12, 4.3f, 0.79f, listOf("Баллы", "Сертификаты")),
)

data class StubAchievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val date: String,
    val points: Int
)

val STUB_ACHIEVEMENTS = listOf(
    StubAchievement("1", "Первый шаг", "Участие в первом мероприятии", "🏅", "2026-03-23", 50),
    StubAchievement("2", "IT-энтузиаст", "5 мероприятий в направлении IT", "💻", "2026-03-22", 100),
    StubAchievement("3", "Активист", "10 мероприятий за год", "⚡", "2026-01-20", 150),
    StubAchievement("4", "Хакатонщик", "Победа в хакатоне", "🏆", "2026-02-14", 200),
    StubAchievement("5", "Медиамастер", "3 медиа-мероприятия", "🎬", "2026-03-05", 80),
)

data class StubMessage(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val text: String,
    val time: String
)

data class StubChat(
    val eventId: String,
    val eventTitle: String,
    val organizerName: String,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0,
    val messages: List<StubMessage>
)

val STUB_CHATS = listOf(
    StubChat(
        eventId = "1",
        eventTitle = "Хакатон по AI",
        organizerName = "Иванов А.",
        lastMessage = "Не забудьте взять ноутбук!",
        time = "10:30",
        unread = 2,
        messages = listOf(
            StubMessage("1", "Иванов А.", "organizer", "Добро пожаловать на Хакатон по AI! Рады видеть вас в числе участников.", "09:00"),
            StubMessage("2", "Иванов А.", "organizer", "Задание: разработать прототип AI-решения для оптимизации городской логистики за 48 часов.", "09:05"),
            StubMessage("3", "Иванов А.", "organizer", "Команды по 3–5 человек. Распределение команд в 10:00 в холле.", "09:10"),
            StubMessage("4", "Иванов А.", "organizer", "Не забудьте взять ноутбук!", "10:30"),
        )
    ),
    StubChat(
        eventId = "3",
        eventTitle = "Медиашкола",
        organizerName = "Сидорова М.",
        lastMessage = "Ссылка на Zoom: zoom.us/j/123456",
        time = "Вчера",
        unread = 0,
        messages = listOf(
            StubMessage("1", "Сидорова М.", "organizer", "Привет! Медиашкола пройдёт онлайн.", "14:00"),
            StubMessage("2", "Сидорова М.", "organizer", "Тема занятия: основы сторителлинга и работа с аудиторией.", "14:02"),
            StubMessage("3", "Сидорова М.", "organizer", "Ссылка на Zoom: zoom.us/j/123456", "14:05"),
        )
    ),
    StubChat(
        eventId = "5",
        eventTitle = "Форум молодёжи",
        organizerName = "Новикова А.",
        lastMessage = "Программа форума прикреплена ниже",
        time = "Пн",
        unread = 1,
        messages = listOf(
            StubMessage("1", "Новикова А.", "organizer", "Здравствуйте! Ваша заявка на Форум молодёжи одобрена.", "11:00"),
            StubMessage("2", "Новикова А.", "organizer", "Программа форума прикреплена ниже", "11:30"),
        )
    ),
)

val DIFFICULTY_COEFFICIENTS = mapOf(1 to 1.0f, 2 to 1.2f, 3 to 1.5f, 4 to 2.0f, 5 to 3.0f)

fun calculateRating(points: Int, difficulty: Int): Int =
    (points * (DIFFICULTY_COEFFICIENTS[difficulty] ?: 1.0f)).toInt()

// ─── АНТИЧИТ: модели ─────────────────────────────────────────────────────────

enum class BanStatus { CLEAN, SUSPICIOUS, BANNED }

data class AnticheatUser(
    val id: String,
    val name: String,
    val username: String,
    val pointsPerDay: Float,      // баллов в день — аномалия если > 80
    val eventsIn24h: Int,         // событий за 24ч — аномалия если > 5
    val duplicateIp: Boolean,     // несколько аккаунтов с одного IP
    val rapidLevelUp: Boolean,    // уровень вырос за < 3 дней
    val rating: Int,
    val status: BanStatus,
    val banReason: String? = null
)

private val FIRST_NAMES = listOf(
    "Алексей","Мария","Дмитрий","Анна","Иван","Ольга","Сергей","Елена","Павел","Наталья",
    "Андрей","Юлия","Михаил","Татьяна","Николай","Ирина","Артём","Светлана","Роман","Екатерина",
    "Кирилл","Валерия","Максим","Дарья","Владимир","Алина","Евгений","Виктория","Тимур","Полина"
)
private val LAST_NAMES = listOf(
    "Иванов","Петров","Сидоров","Козлов","Новиков","Морозов","Волков","Лебедев","Соколов","Попов",
    "Смирнов","Кузнецов","Васильев","Зайцев","Орлов","Фёдоров","Белов","Громов","Тихонов","Ершов",
    "Захаров","Никитин","Степанов","Борисов","Яковлев","Макаров","Андреев","Алексеев","Фролов","Гусев"
)

// Генерируем 500 пользователей детерминированно (без Random — воспроизводимо)
val STUB_ANTICHEAT_USERS: List<AnticheatUser> = (1..500).map { i ->
    val firstName = FIRST_NAMES[i % FIRST_NAMES.size]
    val lastName = LAST_NAMES[(i * 7) % LAST_NAMES.size]
    val name = "$firstName $lastName"
    val username = "${firstName.lowercase()}_${i}"

    // Детерминированные "аномалии" для ~8% пользователей
    val isBanned = i % 13 == 0
    val isSuspicious = !isBanned && i % 7 == 0
    val pointsPerDay = when {
        isBanned -> 95f + (i % 50)
        isSuspicious -> 65f + (i % 20)
        else -> 5f + (i % 40)
    }
    val eventsIn24h = when {
        isBanned -> 7 + (i % 5)
        isSuspicious -> 5
        else -> i % 3
    }
    val duplicateIp = isBanned && i % 26 == 0
    val rapidLevelUp = isBanned && i % 39 == 0

    val banReason = when {
        isBanned && duplicateIp -> "Множественные аккаунты с одного IP-адреса"
        isBanned && rapidLevelUp -> "Подозрительно быстрый рост уровня (< 3 дней)"
        isBanned -> "Аномальное количество баллов: ${pointsPerDay.toInt()} баллов/день (лимит 80)"
        isSuspicious -> "Подозрительная активность: ${eventsIn24h} событий за 24ч"
        else -> null
    }

    AnticheatUser(
        id = "$i",
        name = name,
        username = username,
        pointsPerDay = pointsPerDay,
        eventsIn24h = eventsIn24h,
        duplicateIp = duplicateIp,
        rapidLevelUp = rapidLevelUp,
        rating = 50 + (i * 13) % 1600,
        status = when {
            isBanned -> BanStatus.BANNED
            isSuspicious -> BanStatus.SUSPICIOUS
            else -> BanStatus.CLEAN
        },
        banReason = banReason
    )
}
