package com.cronos.app.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.repository.AiRepository
import com.cronos.app.data.stub.STUB_ACHIEVEMENTS
import com.cronos.app.data.stub.STUB_EVENTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiHubState(
    val resumeLoading: Boolean = false,
    val resumeResult: String? = null,
    val eventsLoading: Boolean = false,
    val eventsResult: String? = null,
    val growthLoading: Boolean = false,
    val growthResult: String? = null,
)

@HiltViewModel
class AiHubViewModel @Inject constructor(
    private val ai: AiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiHubState())
    val state: StateFlow<AiHubState> = _state.asStateFlow()

    // Заглушка профиля участника для промптов
    private val stubProfile = mapOf(
        "name" to "Алексей Иванов",
        "level" to "Gold",
        "rating" to "870",
        "events" to "11",
        "direction" to "IT",
        "city" to "Воронеж",
        "achievements" to STUB_ACHIEVEMENTS.joinToString { "${it.icon} ${it.title} (+${it.points} баллов)" }
    )

    fun generateResume() {
        viewModelScope.launch {
            _state.value = _state.value.copy(resumeLoading = true, resumeResult = null)
            val system = """Ты помощник платформы CRONOS для молодёжных мероприятий. 
                |Пиши на русском языке. Будь конкретным и вдохновляющим. Максимум 200 слов.""".trimMargin()
            val user = """Составь профессиональное резюме-самопрезентацию для участника:
                |Имя: ${stubProfile["name"]}
                |Уровень: ${stubProfile["level"]} (${stubProfile["rating"]} баллов)
                |Мероприятий: ${stubProfile["events"]}
                |Направление: ${stubProfile["direction"]}, город: ${stubProfile["city"]}
                |Достижения: ${stubProfile["achievements"]}
                |Формат: 3-4 предложения, подходящие для портфолио или резюме.""".trimMargin()
            ai.ask(system, user)
                .onSuccess { _state.value = _state.value.copy(resumeResult = it) }
                .onFailure { _state.value = _state.value.copy(resumeResult = "Ошибка: ${it.message}") }
            _state.value = _state.value.copy(resumeLoading = false)
        }
    }

    fun suggestEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(eventsLoading = true, eventsResult = null)
            val eventsList = STUB_EVENTS.take(8).joinToString("\n") {
                "- ${it.title} (${it.direction}, ${it.points} баллов, сложность ${it.difficulty}/5)"
            }
            val system = """Ты AI-советник платформы CRONOS. Пиши на русском. Будь конкретным."""
            val user = """Участник: ${stubProfile["name"]}, уровень ${stubProfile["level"]}, 
                |направление ${stubProfile["direction"]}, ${stubProfile["rating"]} баллов.
                |
                |Доступные мероприятия:
                |$eventsList
                |
                |Выбери топ-3 подходящих мероприятия и объясни почему каждое подходит этому участнику.
                |Формат: пронумерованный список с кратким обоснованием.""".trimMargin()
            ai.ask(system, user)
                .onSuccess { _state.value = _state.value.copy(eventsResult = it) }
                .onFailure { _state.value = _state.value.copy(eventsResult = "Ошибка: ${it.message}") }
            _state.value = _state.value.copy(eventsLoading = false)
        }
    }

    fun buildGrowthPlan() {
        viewModelScope.launch {
            _state.value = _state.value.copy(growthLoading = true, growthResult = null)
            val system = """Ты карьерный советник платформы CRONOS. Пиши на русском. 
                |Давай конкретные цифры и сроки.""".trimMargin()
            val user = """Участник ${stubProfile["name"]} хочет попасть в уровень Reserve.
                |Текущий уровень: ${stubProfile["level"]}, баллов: ${stubProfile["rating"]}.
                |До Reserve нужно 1250 баллов. Осталось: ${1250 - (stubProfile["rating"]?.toIntOrNull() ?: 870)} баллов.
                |Направление: ${stubProfile["direction"]}.
                |
                |Составь конкретный план: какие типы мероприятий посещать, сколько в неделю, 
                |за сколько недель реально достичь цели. Максимум 150 слов.""".trimMargin()
            ai.ask(system, user)
                .onSuccess { _state.value = _state.value.copy(growthResult = it) }
                .onFailure { _state.value = _state.value.copy(growthResult = "Ошибка: ${it.message}") }
            _state.value = _state.value.copy(growthLoading = false)
        }
    }
}
