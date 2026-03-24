package com.cronos.app.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

// Заглушка: рост рейтинга по месяцам
private val ratingHistory = entryModelOf(
    listOf(
        entryOf(0f, 0f), entryOf(1f, 80f), entryOf(2f, 150f),
        entryOf(3f, 210f), entryOf(4f, 320f), entryOf(5f, 410f),
        entryOf(6f, 480f), entryOf(7f, 520f), entryOf(8f, 600f),
        entryOf(9f, 680f), entryOf(10f, 750f), entryOf(11f, 820f),
    )
)
private val monthLabels = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

private val levels = listOf(
    "Bronze" to 0..199,
    "Silver" to 200..499,
    "Gold" to 500..999,
    "Reserve" to 1000..Int.MAX_VALUE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController) {
    val currentPoints = 320 // TODO: из профиля

    val currentLevelIndex = levels.indexOfFirst { currentPoints in it.second }.coerceAtLeast(0)
    val currentLevel = levels[currentLevelIndex].first
    val nextLevel = levels.getOrNull(currentLevelIndex + 1)

    val progressToNext = nextLevel?.let {
        val start = levels[currentLevelIndex].second.first.toFloat()
        val end = it.second.first.toFloat()
        (currentPoints - start) / (end - start)
    } ?: 1f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналитика") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Уровень
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ваш уровень", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        levels.forEach { (name, _) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (name == currentLevel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (name == currentLevel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(progress = progressToNext.coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth())
                    Text("$currentPoints баллов · Уровень: $currentLevel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    nextLevel?.let {
                        Text("До ${it.first}: ${it.second.first - currentPoints} баллов", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Прогноз
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Прогноз", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    val reserveThreshold = 1000
                    val toReserve = (reserveThreshold - currentPoints).coerceAtLeast(0)
                    val probability = ((currentPoints.toFloat() / reserveThreshold) * 100).toInt().coerceAtMost(100)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("До кадрового резерва:", style = MaterialTheme.typography.bodyMedium)
                        Text("$toReserve баллов", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Вероятность попадания:", style = MaterialTheme.typography.bodyMedium)
                        Text("$probability%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(progress = probability / 100f, modifier = Modifier.fillMaxWidth())
                }
            }

            // График роста рейтинга
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Рост рейтинга", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Chart(
                        chart = lineChart(),
                        model = ratingHistory,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = { value, _ -> monthLabels.getOrElse(value.toInt()) { "" } }
                        ),
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            }
        }
    }
}
