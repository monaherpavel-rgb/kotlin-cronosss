package com.cronos.app.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiHubScreen(navController: NavController, viewModel: AiHubViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI-ассистент") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            "Powered by Qwen 3.5",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "AI-функции для участников платформы CRONOS",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // ── AI-РЕЗЮМЕ ──────────────────────────────────────────────────
            AiCard(
                icon = Icons.Default.Description,
                title = "AI-резюме достижений",
                description = "Генерирует красивую самопрезентацию на основе ваших достижений и баллов",
                buttonText = "Сгенерировать резюме",
                isLoading = state.resumeLoading,
                result = state.resumeResult,
                onAction = { viewModel.generateResume() }
            )

            // ── AI-ПОДБОР СОБЫТИЙ ──────────────────────────────────────────
            AiCard(
                icon = Icons.Default.EventAvailable,
                title = "AI-подбор событий",
                description = "Подберёт топ-3 мероприятия специально под ваш профиль и интересы",
                buttonText = "Подобрать события",
                isLoading = state.eventsLoading,
                result = state.eventsResult,
                onAction = { viewModel.suggestEvents() }
            )

            // ── AI-ПЛАН РОСТА ──────────────────────────────────────────────
            AiCard(
                icon = Icons.Default.TrendingUp,
                title = "AI-план роста",
                description = "Как попасть в Reserve? ИИ строит конкретный план: события, баллы, сроки",
                buttonText = "Построить план",
                isLoading = state.growthLoading,
                result = state.growthResult,
                onAction = { viewModel.buildGrowthPlan() }
            )
        }
    }
}

@Composable
fun AiCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    isLoading: Boolean,
    result: String?,
    onAction: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (result != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        result.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
                             .replace(Regex("\\*(.+?)\\*"), "$1")
                             .replace(Regex("#{1,6}\\s"), "")
                             .trim(),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = onAction,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Анализирую...")
                } else {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(buttonText)
                }
            }
        }
    }
}
