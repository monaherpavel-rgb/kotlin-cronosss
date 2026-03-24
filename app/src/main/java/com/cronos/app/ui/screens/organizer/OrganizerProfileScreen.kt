package com.cronos.app.ui.screens.organizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cronos.app.data.stub.STUB_EVENTS
import com.cronos.app.data.stub.STUB_ORGANIZERS
import com.cronos.app.data.stub.StubOrganizer
import com.cronos.app.ui.navigation.Screen

// Заглушка: организаторы на модерации
private data class PendingOrganizer(val name: String, val org: String, var status: String = "pending")
private val PENDING_ORGANIZERS = mutableListOf(
    PendingOrganizer("Козлов Артём", "Молодёжный центр Казани"),
    PendingOrganizer("Белова Ирина", "IT-Академия"),
    PendingOrganizer("Фёдоров Максим", "Студенческий союз"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerProfileScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Организаторы", "Модерация", "Настройки")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель управления") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CreateEvent.route) }) {
                        Icon(Icons.Default.Add, "Создать мероприятие")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }
            when (selectedTab) {
                0 -> OrganizersTab()
                1 -> ModerationTab()
                2 -> SettingsTab()
            }
        }
    }
}

@Composable
private fun OrganizersTab() {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Активные организаторы", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        items(STUB_ORGANIZERS) { org -> OrganizerCard(org) }
    }
}

@Composable
private fun OrganizerCard(org: StubOrganizer) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(org.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(org.organization, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Trust: ${(org.trustScore * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("★ ${org.avgRating}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF9A825))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Мероприятий: ${org.eventsCount}", style = MaterialTheme.typography.bodySmall)
            }
            LinearProgressIndicator(progress = org.trustScore, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            Text("Типичные призы:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                org.typicalRewards.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
            }
        }
    }
}

@Composable
private fun ModerationTab() {
    val pending = remember { PENDING_ORGANIZERS.toMutableStateList() }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Новые организаторы (${pending.count { it.status == "pending" }})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(pending) { org ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(org.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(org.org, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        when (org.status) {
                            "approved" -> Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            "rejected" -> Icon(Icons.Default.Cancel, null, tint = MaterialTheme.colorScheme.error)
                            else -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { org.status = "approved" }) {
                                    Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50))
                                }
                                IconButton(onClick = { org.status = "rejected" }) {
                                    Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    if (org.status != "pending") {
                        Text(
                            if (org.status == "approved") "Подтверждён" else "Отклонён",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (org.status == "approved") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTab() {
    var itWeight by remember { mutableStateOf(1.0f) }
    var mediaWeight by remember { mutableStateOf(1.0f) }
    var socialWeight by remember { mutableStateOf(1.0f) }
    var maxEventsPerDay by remember { mutableStateOf("3") }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Настройка весов баллов", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeightSlider("IT", itWeight) { itWeight = it }
                    WeightSlider("Медиа", mediaWeight) { mediaWeight = it }
                    WeightSlider("Социальные проекты", socialWeight) { socialWeight = it }
                }
            }
        }
        item { Text("Лимиты", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = maxEventsPerDay,
                        onValueChange = { maxEventsPerDay = it },
                        label = { Text("Макс. мероприятий в день") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Коэффициенты сложности: 1→×1.0 · 2→×1.2 · 3→×1.5 · 4→×2.0 · 5→×3.0",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Button(onClick = { /* TODO: сохранить */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Сохранить настройки")
            }
        }
    }
}

@Composable
private fun WeightSlider(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("×${"%.1f".format(value)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        Slider(value = value, onValueChange = onChange, valueRange = 0.5f..3.0f)
    }
}

private fun <T> List<T>.toMutableStateList() = toMutableList().let { list ->
    androidx.compose.runtime.mutableStateListOf<T>().also { it.addAll(list) }
}
