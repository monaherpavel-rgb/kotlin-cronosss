package com.cronos.app.ui.screens.leaderboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavController
import com.cronos.app.data.stub.STUB_PARTICIPANTS
import com.cronos.app.data.stub.StubParticipant
import com.cronos.app.ui.navigation.Screen

private val DIRECTIONS = listOf("Все", "IT", "Медиа", "Социальные проекты", "Политика", "Наука", "Спорт", "Культура", "Бизнес")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController) {
    var selectedDirection by remember { mutableStateOf("Все") }

    val sorted = STUB_PARTICIPANTS
        .filter { selectedDirection == "Все" || it.direction == selectedDirection }
        .sortedByDescending { it.rating }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Лидерборд · Топ-100") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная") }, selected = false, onClick = { navController.navigate(Screen.Dashboard.route) { launchSingleTop = true } })
                NavigationBarItem(icon = { Icon(Icons.Default.Event, null) }, label = { Text("События") }, selected = false, onClick = { navController.navigate(Screen.Events.route) })
                NavigationBarItem(icon = { Icon(Icons.Default.Leaderboard, null) }, label = { Text("Лидерборд") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Чат") }, selected = false, onClick = { navController.navigate(Screen.Messenger.route) })
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") }, selected = false, onClick = { navController.navigate(Screen.Profile.route) })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DIRECTIONS.forEach { dir ->
                    FilterChip(
                        selected = selectedDirection == dir,
                        onClick = { selectedDirection = dir },
                        label = { Text(dir) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (sorted.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Нет участников", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    itemsIndexed(sorted) { index, participant ->
                        LeaderboardItem(rank = index + 1, participant = participant)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(rank: Int, participant: StubParticipant) {
    val levelColor = when (participant.level) {
        "Reserve" -> Color(0xFF7B1FA2)
        "Gold" -> Color(0xFFF9A825)
        "Silver" -> Color(0xFF78909C)
        else -> Color(0xFF8D6E63)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.Center) {
                when (rank) {
                    1 -> Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                    2 -> Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFC0C0C0), modifier = Modifier.size(24.dp))
                    3 -> Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFCD7F32), modifier = Modifier.size(22.dp))
                    else -> Text("#$rank", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(participant.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("@${participant.username} · ${participant.city}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${participant.eventsCount} мероприятий · ${participant.direction}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${participant.rating}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Surface(color = levelColor, shape = MaterialTheme.shapes.small) {
                    Text(participant.level, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }
    }
}
