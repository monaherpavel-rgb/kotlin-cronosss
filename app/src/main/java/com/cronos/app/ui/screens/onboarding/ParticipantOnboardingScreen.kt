package com.cronos.app.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cronos.app.ui.components.CityDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantOnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(0) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var selectedGoal by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val interests = listOf("IT", "Медиа", "Социальные проекты", "Политика")
    val goals = listOf("Попасть в кадровый резерв", "Получить стажировку", "Развивать навыки")

    LaunchedEffect(uiState.isDone) { if (uiState.isDone) onComplete() }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(progress = (step + 1) / 4f, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text("Шаг ${step + 1} из 4", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            0 -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Расскажите о себе", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Фамилия") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { input ->
                        // Принимаем только цифры и точки/дефисы
                        val digits = input.filter { it.isDigit() }
                        // Автоформат: вставляем точки → ДД.ММ.ГГГГ
                        birthDate = when {
                            digits.length <= 2 -> digits
                            digits.length <= 4 -> "${digits.take(2)}.${digits.drop(2)}"
                            else -> "${digits.take(2)}.${digits.drop(2).take(2)}.${digits.drop(4).take(4)}"
                        }
                    },
                    label = { Text("Дата рождения") },
                    placeholder = { Text("ДД.ММ.ГГГГ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                CityDropdown(value = city, onValueChange = { city = it }, modifier = Modifier.fillMaxWidth())
            }
            1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ваши направления", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Выберите одно или несколько", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                interests.forEach { interest ->
                    FilterChip(
                        selected = interest in selectedInterests,
                        onClick = { selectedInterests = if (interest in selectedInterests) selectedInterests - interest else selectedInterests + interest },
                        label = { Text(interest) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            2 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Чего вы хотите достичь?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                goals.forEach { goal ->
                    OutlinedCard(
                        onClick = { selectedGoal = goal },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(if (selectedGoal == goal) 2.dp else 1.dp, if (selectedGoal == goal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                        colors = CardDefaults.outlinedCardColors(containerColor = if (selectedGoal == goal) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    ) {
                        Text(goal, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            3 -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Вы готовы начать", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Профиль создан. Участвуйте в мероприятиях, набирайте баллы и попадите в кадровый резерв.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (step < 3) step++
                else viewModel.saveParticipant(
                    firstName, lastName,
                    // Конвертируем ДД.ММ.ГГГГ → ГГГГ-ММ-ДД для Supabase
                    birthDate.split(".").let { parts ->
                        if (parts.size == 3 && parts[2].length == 4) "${parts[2]}-${parts[1]}-${parts[0]}"
                        else birthDate
                    },
                    city, selectedInterests.toList(), selectedGoal
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading && when (step) {
                0 -> firstName.isNotBlank() && lastName.isNotBlank() && city.isNotBlank()
                1 -> selectedInterests.isNotEmpty()
                2 -> selectedGoal.isNotEmpty()
                else -> true
            }
        ) {
            if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text(if (step < 3) "Далее" else "Перейти в платформу")
        }
    }
}
