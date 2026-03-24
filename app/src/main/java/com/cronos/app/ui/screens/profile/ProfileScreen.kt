package com.cronos.app.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cronos.app.data.model.EventApplication
import com.cronos.app.data.model.PortfolioItem
import com.cronos.app.data.model.Profile
import com.cronos.app.data.stub.STUB_ACHIEVEMENTS
import com.cronos.app.data.stub.StubAchievement
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

// Заглушка: участия по месяцам
private val participationByMonth = entryModelOf(
    listOf(
        entryOf(0f, 1f), entryOf(1f, 2f), entryOf(2f, 1f),
        entryOf(3f, 3f), entryOf(4f, 2f), entryOf(5f, 4f),
        entryOf(6f, 3f), entryOf(7f, 5f), entryOf(8f, 2f),
        entryOf(9f, 4f), entryOf(10f, 3f), entryOf(11f, 6f),
    )
)
private val monthLabels = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Профиль", "Достижения", "Портфолио", "Мои события")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(uiState.error!!, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                uiState.profile != null -> Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { i, title ->
                            Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                        }
                    }
                    when (selectedTab) {
                        0 -> ProfileTab(uiState.profile!!, viewModel)
                        1 -> AchievementsTab()
                        2 -> PortfolioTab(uiState.portfolio, viewModel)
                        3 -> MyEventsTab(uiState.myEvents)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(profile: Profile, viewModel: ProfileViewModel) {
    var showUsernameDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.saveAvatarUrl(it.toString()) }
    }

    val totalEvents = 12
    val totalPoints = 320
    val rank = 7
    val level = "Gold"
    val nextLevelPoints = 500
    val progress = totalPoints.toFloat() / nextLevelPoints

    val levelColor = when (level) {
        "Reserve" -> Color(0xFF7B1FA2)
        "Gold"    -> Color(0xFFF9A825)
        "Silver"  -> Color(0xFF78909C)
        else      -> Color(0xFF8D6E63)
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── HERO БЛОК ──────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 28.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Аватар
                    Box(contentAlignment = Alignment.BottomEnd) {
                        if (profile.avatarUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(profile.avatarUrl).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(96.dp).clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(96.dp).clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(3.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(52.dp), tint = Color.White)
                            }
                        }
                        IconButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        }
                    }

                    // Имя
                    Text(
                        profile.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Username + город
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "@${profile.username ?: "username"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        IconButton(onClick = { showUsernameDialog = true }, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(12.dp), tint = Color.White.copy(alpha = 0.7f))
                        }
                        if (profile.city != null) {
                            Text("·", color = Color.White.copy(alpha = 0.5f))
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.White.copy(alpha = 0.7f))
                            Text(profile.city, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                        }
                    }

                    // Уровень-бейдж
                    Surface(
                        color = levelColor,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Text(level, style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── СТАТИСТИКА ─────────────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-16).dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        ProfileStatItem("$totalPoints", "Баллы", Icons.Default.Star, MaterialTheme.colorScheme.primary)
                        VerticalDividerLine()
                        ProfileStatItem("#$rank", "Место", Icons.Default.Leaderboard, Color(0xFFF9A825))
                        VerticalDividerLine()
                        ProfileStatItem("$totalEvents", "Событий", Icons.Default.Event, Color(0xFF4CAF50))
                        VerticalDividerLine()
                        ProfileStatItem(level, "Уровень", Icons.Default.Shield, levelColor)
                    }

                    // Прогресс до следующего уровня
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "До Reserve",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$totalPoints / $nextLevelPoints баллов",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            "Осталось ${nextLevelPoints - totalPoints} баллов",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── ГРАФИК ─────────────────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Активность", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Участия по месяцам · 2025–2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "$totalEvents событий",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Chart(
                        chart = columnChart(),
                        model = participationByMonth,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = { value, _ -> monthLabels.getOrElse(value.toInt()) { "" } }
                        ),
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }
        }

        // ── НАПРАВЛЕНИЯ ────────────────────────────────────────────────────
        if (profile.interests?.isNotEmpty() == true) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Направления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.interests.forEach {
                                AssistChip(onClick = {}, label = { Text(it) })
                            }
                        }
                    }
                }
            }
        }

        // ── СТАТУС АККАУНТА ────────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Статус аккаунта", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(
                            when (profile.verificationStatus) {
                                "approved" -> "Подтверждён администратором"
                                "pending"  -> "Ожидает подтверждения"
                                else       -> "Отклонён"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        when (profile.verificationStatus) {
                            "approved" -> Icons.Default.CheckCircle
                            "pending"  -> Icons.Default.HourglassEmpty
                            else       -> Icons.Default.Cancel
                        },
                        null,
                        tint = when (profile.verificationStatus) {
                            "approved" -> Color(0xFF4CAF50)
                            "pending"  -> Color(0xFFFF9800)
                            else       -> MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    if (showUsernameDialog) {
        UsernameDialog(
            current = profile.username ?: "",
            onConfirm = { viewModel.saveUsername(it); showUsernameDialog = false },
            onDismiss = { showUsernameDialog = false }
        )
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun VerticalDividerLine() {
    Box(modifier = Modifier.width(1.dp).height(48.dp).background(MaterialTheme.colorScheme.outlineVariant))
}

@Composable
private fun AchievementsTab() {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${STUB_ACHIEVEMENTS.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Достижений", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${STUB_ACHIEVEMENTS.sumOf { it.points }}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Бонус баллов", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        items(STUB_ACHIEVEMENTS) { achievement -> AchievementCard(achievement) }
    }
}

@Composable
private fun AchievementCard(achievement: StubAchievement) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(achievement.icon, style = MaterialTheme.typography.displaySmall)
            Column(modifier = Modifier.weight(1f)) {
                Text(achievement.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(achievement.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(achievement.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                Text(
                    "+${achievement.points}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PortfolioTab(items: List<PortfolioItem>, viewModel: ProfileViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Button(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить проект")
            }
        }
        if (items.isEmpty()) {
            item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Портфолио пусто", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        } else {
            items(items) { item -> PortfolioCard(item) }
        }
    }
    if (showAddDialog) {
        AddPortfolioDialog(
            onConfirm = { title, desc, url -> viewModel.addPortfolioItem(title, desc, url); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun PortfolioCard(item: PortfolioItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (item.imageUrl != null) {
                AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(8.dp)))
            }
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            item.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            item.projectUrl?.let {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Link, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun MyEventsTab(events: List<EventApplication>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (events.isEmpty()) {
            item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Нет поданных заявок", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        } else {
            items(events) { event ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(event.eventTitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("${event.direction} · ${event.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text(when (event.status) { "approved" -> "Принят"; "rejected" -> "Отклонён"; else -> "На рассмотрении" }) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = when (event.status) {
                                "approved" -> Color(0xFF1B5E20); "rejected" -> Color(0xFF7F0000); else -> MaterialTheme.colorScheme.surfaceVariant
                            })
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsernameDialog(current: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var value by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить username") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it.filter { c -> c.isLetterOrDigit() || c == '_' } },
                label = { Text("@username") }, singleLine = true, prefix = { Text("@") }
            )
        },
        confirmButton = { TextButton(onClick = { if (value.isNotBlank()) onConfirm(value) }) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun AddPortfolioDialog(onConfirm: (String, String, String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить проект") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Ссылка") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { if (title.isNotBlank()) onConfirm(title, description, url) }) { Text("Добавить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
