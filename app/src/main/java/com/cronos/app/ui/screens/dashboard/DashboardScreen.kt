package com.cronos.app.ui.screens.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cronos.app.data.stub.STUB_EVENTS
import com.cronos.app.data.stub.STUB_ORGANIZERS
import com.cronos.app.data.stub.StubEvent
import com.cronos.app.ui.navigation.Screen

private val TAG_CLOUD = listOf(
    "IT" to 18, "Медиа" to 12, "Социальные проекты" to 9,
    "Политика" to 7, "Наука" to 5, "Бизнес" to 8, "Спорт" to 4, "Культура" to 3
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val role = uiState.profile?.role ?: "participant"

    // Администратор получает полностью отдельный Scaffold
    if (role == "admin") {
        AdminDashboardScaffold(navController, uiState)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CRONOS") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Stats.route) }) {
                        Icon(Icons.Default.BarChart, "Статистика")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Event, null) }, label = { Text("События") }, selected = false, onClick = { navController.navigate(Screen.Events.route) })
                NavigationBarItem(icon = { Icon(Icons.Default.Leaderboard, null) }, label = { Text("Лидерборд") }, selected = false, onClick = { navController.navigate(Screen.Leaderboard.route) })
                NavigationBarItem(icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Чат") }, selected = false, onClick = { navController.navigate(Screen.Messenger.route) })
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") }, selected = false, onClick = { navController.navigate(Screen.Profile.route) })
            }
        }
    ) { padding ->
        when (role) {
            "organizer" -> OrganizerDashboard(navController, uiState, padding)
            "observer" -> ObserverDashboard(navController, uiState, padding)
            else -> ParticipantDashboard(navController, uiState, padding)
        }
    }
}

// ─── УЧАСТНИК ────────────────────────────────────────────────────────────────

@Composable
private fun ParticipantDashboard(navController: NavController, uiState: DashboardUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val name = uiState.profile?.displayName ?: "..."
            Text("Привет, $name!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        // Карточка статистики участника
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatMini("320", "Баллов")
                    StatMini("#7", "Место")
                    StatMini("Gold", "Уровень")
                    StatMini("12", "Событий")
                }
            }
        }

        // Прогресс до следующего уровня
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("До Reserve", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("680 баллов", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(progress = 0.32f, modifier = Modifier.fillMaxWidth())
                    TextButton(
                        onClick = { navController.navigate(Screen.Stats.route) },
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("Подробная аналитика →") }
                }
            }
        }

        // Популярные направления
        item {
            Text("Популярные направления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TAG_CLOUD.sortedByDescending { it.second }.forEach { (tag, count) ->
                    val fontSize = (10 + count / 2).coerceIn(10, 18)
                    AssistChip(onClick = { navController.navigate(Screen.Events.route) }, label = { Text(tag, fontSize = fontSize.sp) })
                }
            }
        }

        // Ближайшие события
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Ближайшие события", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { navController.navigate(Screen.Events.route) }) { Text("Все") }
            }
        }
        items(STUB_EVENTS.take(4)) { event -> DashboardEventCard(event) }

        // AI-ассистент
        item {
            Button(
                onClick = { navController.navigate(Screen.AiHub.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI-ассистент CRONOS")
            }
        }
    }
}

// ─── ОРГАНИЗАТОР ─────────────────────────────────────────────────────────────

@Composable
private fun OrganizerDashboard(navController: NavController, uiState: DashboardUiState, padding: PaddingValues) {
    // Берём первого организатора из стаба как публичный профиль текущего пользователя
    val org = STUB_ORGANIZERS.first()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Профиль") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Панель") })
        }
        when (selectedTab) {
            0 -> OrganizerPublicProfile(org, navController)
            1 -> OrganizerAdminPanel(navController)
        }
    }
}

@Composable
private fun OrganizerPublicProfile(org: com.cronos.app.data.stub.StubOrganizer, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Шапка профиля
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(org.name.first().toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Column {
                            Text(org.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(org.organization, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Организатор", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        StatMini("${org.eventsCount}", "Мероприятий")
                        StatMini("★ ${org.avgRating}", "Рейтинг")
                        StatMini("${(org.trustScore * 100).toInt()}%", "Доверие")
                    }
                }
            }
        }

        // Рейтинг доверия
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Рейтинг доверия", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "На основе ${org.eventsCount * 8} отзывов участников",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinearProgressIndicator(
                        progress = org.trustScore,
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            org.trustScore >= 0.9f -> Color(0xFF4CAF50)
                            org.trustScore >= 0.7f -> Color(0xFFF9A825)
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                    Text(
                        "${(org.trustScore * 100).toInt()}% участников рекомендуют этого организатора",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Типичные призы
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Типичные призы", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${org.name} обычно награждает участников:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        org.typicalRewards.forEach { reward ->
                            AssistChip(
                                onClick = {},
                                label = { Text(reward) },
                                leadingIcon = { Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }
        }

        // Последние мероприятия
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Мои мероприятия", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { navController.navigate(Screen.Events.route) }) { Text("Все") }
            }
        }
        items(STUB_EVENTS.take(3)) { event -> DashboardEventCard(event) }
    }
}

@Composable
private fun OrganizerAdminPanel(navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Панель управления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        // Создать мероприятие
        item {
            Button(onClick = { navController.navigate(Screen.CreateEvent.route) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать мероприятие")
            }
        }

        // QR-сканер
        item {
            OutlinedButton(onClick = { navController.navigate(Screen.QrScanner.route) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("QR-сканер участников")
            }
        }

        item { Divider() }
        item { Text("Модерация и настройки", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }

        // Модерация организаторов
        item {
            OutlinedButton(onClick = { navController.navigate(Screen.OrganizerProfile.route) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Модерация организаторов")
            }
        }

        // Настройка весов
        item {
            OutlinedButton(onClick = { navController.navigate(Screen.OrganizerProfile.route) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Настройка весов баллов")
            }
        }

        item { Divider() }

        // Быстрая статистика
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Статистика мероприятий", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        StatMini("24", "Всего")
                        StatMini("3", "Активных")
                        StatMini("312", "Участников")
                        StatMini("4.8★", "Рейтинг")
                    }
                }
            }
        }
    }
}

// ─── НАБЛЮДАТЕЛЬ ─────────────────────────────────────────────────────────────

@Composable
private fun ObserverDashboard(navController: NavController, uiState: DashboardUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val name = uiState.profile?.displayName ?: "..."
            Text("Добро пожаловать, $name!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Наблюдатель · Кадровая служба", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }

        // Сводка кадрового резерва
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Кадровый резерв", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        StatMini("10", "Кандидатов")
                        StatMini("3", "Reserve")
                        StatMini("4", "Gold")
                        StatMini("3", "Silver")
                    }
                }
            }
        }

        // Быстрые действия наблюдателя
        item {
            Text("Инструменты", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate(Screen.Inspector.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Инспектор резерва")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Leaderboard.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Leaderboard, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Таблица лидеров")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Rating.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.BarChart, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Рейтинг участников")
                }
            }
        }

        // Топ кандидаты
        item {
            Text("Топ кандидаты", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(STUB_EVENTS.take(3)) { event -> DashboardEventCard(event) }
    }
}

// ─── АДМИНИСТРАТОР ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardScaffold(navController: NavController, uiState: DashboardUiState) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CRONOS · Администратор") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AdminProfile.route) }) {
                        Icon(Icons.Default.AdminPanelSettings, "Профиль")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Главная") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, null) },
                    label = { Text("Чат") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Профиль") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> AdminDashboard(navController, uiState, padding)
            1 -> AdminMessengerInline(navController, padding)
            2 -> AdminProfileInline(navController, padding)
        }
    }
}

@Composable
private fun AdminMessengerInline(navController: NavController, padding: PaddingValues) {
    // Перенаправляем на отдельный экран чата с модераторами
    LaunchedEffect(Unit) { navController.navigate(Screen.AdminMessenger.route) }
    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AdminProfileInline(navController: NavController, padding: PaddingValues) {
    LaunchedEffect(Unit) { navController.navigate(Screen.AdminProfile.route) }
    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AdminDashboard(navController: NavController, uiState: DashboardUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Панель администратора", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("admin@gmail.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }

        // Общая статистика платформы
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Статистика платформы", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        StatMini("10", "Участников")
                        StatMini("3", "Организаторов")
                        StatMini("12", "Событий")
                        StatMini("3", "На модерации")
                    }
                }
            }
        }

        // Инструменты администратора (без Events и Leaderboard)
        item {
            Text("Управление", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate(Screen.OrganizerProfile.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Модерация организаторов")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Inspector.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Инспектор резерва")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.AdminProfile.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Назначить модератора")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Stats.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.BarChart, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Статистика платформы")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Anticheat.route) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Security, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Античит")
                }
            }
        }
    }
}

// ─── Общие компоненты ─────────────────────────────────────────────────────────

@Composable
private fun StatMini(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DashboardEventCard(event: StubEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(event.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(event.direction) })
                AssistChip(onClick = {}, label = { Text(if (event.format == "online") "Онлайн" else "Офлайн") })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${event.points} баллов · Сложность ${event.difficulty}/5", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${event.currentParticipants}/${event.maxParticipants}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
