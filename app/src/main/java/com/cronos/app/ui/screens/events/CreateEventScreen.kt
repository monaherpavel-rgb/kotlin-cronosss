package com.cronos.app.ui.screens.events

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private val ALL_DIRECTIONS = listOf("IT", "Медиа", "Социальные проекты", "Политика", "Наука", "Спорт", "Культура", "Бизнес")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("offline") }
    var direction by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(1f) }
    var maxParticipants by remember { mutableStateOf("50") }
    var points by remember { mutableStateOf("") }

    val formats = listOf("offline" to "Офлайн", "online" to "Онлайн")
    val isValid = title.isNotBlank() && date.isNotBlank() && direction.isNotBlank() && points.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать мероприятие") },
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
            Text("Основная информация", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Название") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )
            OutlinedTextField(
                value = date, onValueChange = { date = it },
                label = { Text("Дата (ГГГГ-ММ-ДД)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Text("Формат", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                formats.forEach { (value, label) ->
                    FilterChip(selected = format == value, onClick = { format = value }, label = { Text(label) })
                }
            }

            Text("Направление", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ALL_DIRECTIONS.forEach { dir ->
                    FilterChip(selected = direction == dir, onClick = { direction = dir }, label = { Text(dir) })
                }
            }

            Text("Сложность: ${difficulty.toInt()}/5", style = MaterialTheme.typography.bodyMedium)
            Slider(value = difficulty, onValueChange = { difficulty = it }, valueRange = 1f..5f, steps = 3)

            OutlinedTextField(
                value = maxParticipants, onValueChange = { maxParticipants = it },
                label = { Text("Макс. участников") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = points, onValueChange = { points = it },
                label = { Text("Баллы за участие") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.saveEvent(
                        title = title,
                        description = description,
                        date = date,
                        format = format,
                        direction = direction,
                        difficulty = difficulty.toInt(),
                        maxParticipants = maxParticipants.toIntOrNull() ?: 50,
                        points = points.toIntOrNull() ?: 0
                    )
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isValid
            ) {
                Text("Создать мероприятие")
            }
        }
    }
}
