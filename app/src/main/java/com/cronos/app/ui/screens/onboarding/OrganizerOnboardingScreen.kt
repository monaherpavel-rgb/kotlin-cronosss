package com.cronos.app.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerOnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(0) }
    var firstName by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var eventTypes by remember { mutableStateOf(setOf<String>()) }

    val uiState by viewModel.uiState.collectAsState()
    val types = listOf("IT", "Медиа", "Социальные проекты", "Политика", "Образование")

    LaunchedEffect(uiState.isDone) { if (uiState.isDone) onComplete() }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(progress = (step + 1) / 3f, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text("Шаг ${step + 1} из 3", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            0 -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Данные организатора", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Организация") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Должность") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Тип мероприятий", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                types.forEach { type ->
                    FilterChip(
                        selected = type in eventTypes,
                        onClick = { eventTypes = if (type in eventTypes) eventTypes - type else eventTypes + type },
                        label = { Text(type) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            2 -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Заявка отправлена", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("На проверке", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Ваша заявка отправлена на модерацию. Обычно это занимает 1-2 рабочих дня.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (step < 2) step++
                else viewModel.saveOrganizer(firstName, organization, position, eventTypes.toList())
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading && when (step) {
                0 -> firstName.isNotBlank() && organization.isNotBlank()
                else -> true
            }
        ) {
            if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text(if (step < 2) "Далее" else "Перейти в платформу")
        }
    }
}
