package com.cronos.app.ui.screens.events

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cronos.app.data.stub.StubEvent
import com.cronos.app.ui.navigation.Screen

private val ALL_DIRECTIONS = listOf(
    "Все", "IT", "Медиа", "Социальные проекты",
    "Политика", "Наука", "Спорт", "Культура", "Бизнес"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: EventsViewModel = hiltViewModel()
) {
    var selectedDirection by remember { mutableStateOf("Все") }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val allEvents by viewModel.events.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val appliedIds = applications.map { it.eventId }.toSet()

    val filtered = if (selectedDirection == "Все") allEvents
    else allEvents.filter { it.direction == selectedDirection }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("События") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.QrScanner.route) }) {
                        Icon(Icons.Default.QrCodeScanner, "Сканировать QR")
                    }
                    IconButton(onClick = { navController.navigate(Screen.CreateEvent.route) }) {
                        Icon(Icons.Default.Add, "Создать")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ALL_DIRECTIONS.forEach { dir ->
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
                items(filtered, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        applied = event.id in appliedIds,
                        onApply = {
                            viewModel.apply(event)
                            snackbarMessage = "Заявка на «${event.title}» подана"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: StubEvent, applied: Boolean, onApply: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(event.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (event.description.isNotBlank()) {
                Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(event.direction) })
                AssistChip(onClick = {}, label = { Text(if (event.format == "online") "Онлайн" else "Офлайн") })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${event.points} баллов · Сложность ${event.difficulty}/5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (applied) {
                    AssistChip(onClick = {}, label = { Text("Заявка подана") })
                } else {
                    Button(
                        onClick = onApply,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Участвовать")
                    }
                }
            }
        }
    }
}
