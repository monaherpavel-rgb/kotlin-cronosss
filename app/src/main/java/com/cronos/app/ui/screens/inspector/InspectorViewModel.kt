package com.cronos.app.ui.screens.inspector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.repository.AiRepository
import com.cronos.app.data.stub.StubParticipant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScoringState(val isLoading: Boolean = false, val result: String? = null)
data class CompareState(val isLoading: Boolean = false, val result: String? = null)

@HiltViewModel
class InspectorViewModel @Inject constructor(
    private val ai: AiRepository
) : ViewModel() {

    private val _scoringStates = MutableStateFlow<Map<String, ScoringState>>(emptyMap())
    val scoringStates: StateFlow<Map<String, ScoringState>> = _scoringStates.asStateFlow()

    private val _compareState = MutableStateFlow(CompareState())
    val compareState: StateFlow<CompareState> = _compareState.asStateFlow()

    fun scoreCandidate(candidate: StubParticipant) {
        viewModelScope.launch {
            _scoringStates.value = _scoringStates.value + (candidate.id to ScoringState(isLoading = true))
            val system = "Ты эксперт кадрового резерва платформы CRONOS. Пиши на русском, кратко."
            val user = """Оцени кандидата для включения в кадровый резерв:
                |Имя: ${candidate.name}, ${candidate.age} лет, ${candidate.city}
                |Направление: ${candidate.direction}, уровень: ${candidate.level}
                |Рейтинг: ${candidate.rating}, мероприятий: ${candidate.eventsCount}
                |Достижения: ${candidate.achievements.joinToString { "${it.first} (${it.second} б)" }}
                |
                |Дай оценку потенциала (1-10), риски и рекомендацию: включить/не включить в резерв.
                |Максимум 80 слов.""".trimMargin()
            ai.ask(system, user)
                .onSuccess { _scoringStates.value = _scoringStates.value + (candidate.id to ScoringState(result = it)) }
                .onFailure { _scoringStates.value = _scoringStates.value + (candidate.id to ScoringState(result = "Ошибка: ${it.message}")) }
        }
    }

    fun compareCandidates(a: StubParticipant, b: StubParticipant) {
        viewModelScope.launch {
            _compareState.value = CompareState(isLoading = true)
            val system = """Ты эксперт кадрового резерва платформы CRONOS. 
                |Пиши на русском языке. Не используй markdown-символы (* ** # и т.д.).
                |Пиши структурированно, используй только обычный текст с заголовками через двоеточие.""".trimMargin()
            val user = """Составь подробный сравнительный отчёт двух кандидатов для включения в кадровый резерв.
                |
                |КАНДИДАТ А: ${a.name}
                |Возраст: ${a.age} лет, город: ${a.city}
                |Направление: ${a.direction}, уровень: ${a.level}
                |Рейтинг: ${a.rating} баллов, мероприятий: ${a.eventsCount}
                |Достижения: ${a.achievements.joinToString("; ") { "${it.first} (${it.second} б)" }}
                |Прогноз: ${a.forecast}
                |
                |КАНДИДАТ Б: ${b.name}
                |Возраст: ${b.age} лет, город: ${b.city}
                |Направление: ${b.direction}, уровень: ${b.level}
                |Рейтинг: ${b.rating} баллов, мероприятий: ${b.eventsCount}
                |Достижения: ${b.achievements.joinToString("; ") { "${it.first} (${it.second} б)" }}
                |Прогноз: ${b.forecast}
                |
                |Структура отчёта (каждый раздел подробно, минимум 2-3 предложения):
                |1. Общая характеристика кандидатов
                |2. Сравнение по активности и вовлечённости
                |3. Сравнение по направлению и специализации
                |4. Сравнение достижений и наград
                |5. Оценка потенциала роста каждого
                |6. Риски и слабые стороны каждого кандидата
                |7. Сравнение по географии и охвату
                |8. Итоговая рекомендация: кого включить в резерв и почему
                |9. Что нужно улучшить тому, кто не прошёл отбор
                |
                |Минимум 400 слов. Пиши развёрнуто и профессионально.""".trimMargin()
            ai.ask(system, user)
                .onSuccess { _compareState.value = CompareState(result = it) }
                .onFailure { _compareState.value = CompareState(result = "Ошибка: ${it.message}") }
        }
    }
}
