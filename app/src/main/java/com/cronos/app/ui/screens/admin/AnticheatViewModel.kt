package com.cronos.app.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.repository.AiRepository
import com.cronos.app.data.stub.AnticheatUser
import com.cronos.app.data.stub.BanStatus
import com.cronos.app.data.stub.STUB_ANTICHEAT_USERS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnticheatState(
    val users: List<AnticheatUser> = STUB_ANTICHEAT_USERS.take(30),
    val aiEnabled: Boolean = true,
    val isStreaming: Boolean = false,   // "реалтайм" добавление
    val streamIndex: Int = 30,          // сколько уже загружено
    val aiSummary: String? = null,
    val isSummaryLoading: Boolean = false
)

@HiltViewModel
class AnticheatViewModel @Inject constructor(
    private val ai: AiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnticheatState())
    val state: StateFlow<AnticheatState> = _state.asStateFlow()

    private var streamJob: Job? = null

    init {
        // Запускаем "реалтайм" поток сразу
        startStreaming()
    }

    // Имитация реалтайм-добавления новых пользователей
    fun startStreaming() {
        if (streamJob?.isActive == true) return
        streamJob = viewModelScope.launch {
            _state.value = _state.value.copy(isStreaming = true)
            var idx = _state.value.streamIndex
            while (idx < STUB_ANTICHEAT_USERS.size) {
                delay(800L) // новый пользователь каждые 0.8с
                val newUser = STUB_ANTICHEAT_USERS[idx]
                val currentUsers = _state.value.users.toMutableList()
                currentUsers.add(0, newUser) // добавляем в начало списка

                // Если AI включён — тихо анализируем нового пользователя
                if (_state.value.aiEnabled && newUser.status != BanStatus.CLEAN) {
                    // AI уже "принял решение" — статус уже в данных, просто показываем
                    // В реальном приложении здесь был бы вызов AI
                }

                _state.value = _state.value.copy(users = currentUsers, streamIndex = idx + 1)
                idx++

                // Пауза каждые 10 пользователей
                if (idx % 10 == 0) delay(2000L)
            }
            _state.value = _state.value.copy(isStreaming = false)
        }
    }

    fun toggleAi() {
        val enabling = !_state.value.aiEnabled
        _state.value = _state.value.copy(aiEnabled = enabling)
        if (enabling) {
            // Включили AI — возобновляем стриминг
            startStreaming()
        } else {
            // Выключили AI — останавливаем стриминг
            streamJob?.cancel()
            streamJob = null
            _state.value = _state.value.copy(isStreaming = false)
        }
    }

    // Ручная смена статуса (для ручной проверки)
    fun setUserStatus(userId: String, status: BanStatus, reason: String? = null) {
        val updated = _state.value.users.map { u ->
            if (u.id == userId) u.copy(status = status, banReason = reason ?: u.banReason) else u
        }
        _state.value = _state.value.copy(users = updated)
    }

    // AI-сводка по платформе (по запросу)
    fun runAiSummary() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSummaryLoading = true, aiSummary = null)
            val users = _state.value.users
            val total = users.size
            val banned = users.count { it.status == BanStatus.BANNED }
            val suspicious = users.count { it.status == BanStatus.SUSPICIOUS }
            val topBanned = users.filter { it.status == BanStatus.BANNED }.take(3)
                .joinToString("\n") { "- ${it.name} (@${it.username}): ${it.banReason}" }

            val system = "Ты AI-система безопасности платформы CRONOS. Пиши на русском, кратко и по делу. Не используй markdown-форматирование."
            val user = """Проанализируй данные платформы:
                |Всего пользователей: $total
                |Забанено: $banned (${if (total > 0) banned * 100 / total else 0}%)
                |Подозрительных: $suspicious
                |
                |Топ нарушители:
                |$topBanned
                |
                |Дай краткое заключение о состоянии безопасности и рекомендации. Максимум 100 слов.""".trimMargin()

            ai.ask(system, user)
                .onSuccess { _state.value = _state.value.copy(aiSummary = stripMarkdown(it)) }
                .onFailure { _state.value = _state.value.copy(aiSummary = "Ошибка AI: ${it.message}") }
            _state.value = _state.value.copy(isSummaryLoading = false)
        }
    }

    private fun stripMarkdown(text: String): String =
        text.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
            .replace(Regex("\\*(.+?)\\*"), "$1")
            .replace(Regex("#{1,6}\\s"), "")
            .replace(Regex("`(.+?)`"), "$1")
            .trim()
}
