package com.cronos.app.data.repository

import com.cronos.app.data.model.EventApplication
import com.cronos.app.data.stub.StubEvent
import com.cronos.app.data.stub.STUB_EVENTS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory shared state — заменить на Supabase когда будут таблицы events / event_participations
 */
@Singleton
class AppStateRepository @Inject constructor() {

    // Локальный admin-режим (без Supabase)
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    fun setAdminMode(enabled: Boolean) { _isAdminMode.value = enabled }

    // Все события (stub + созданные пользователем)
    private val _events = MutableStateFlow(STUB_EVENTS.toMutableList())
    val events: StateFlow<List<StubEvent>> = _events.asStateFlow()

    // Поданные заявки текущего пользователя
    private val _applications = MutableStateFlow<List<EventApplication>>(emptyList())
    val applications: StateFlow<List<EventApplication>> = _applications.asStateFlow()

    fun applyToEvent(event: StubEvent) {
        val current = _applications.value.toMutableList()
        if (current.none { it.eventId == event.id }) {
            current.add(
                EventApplication(
                    eventId = event.id,
                    eventTitle = event.title,
                    direction = event.direction,
                    date = event.date,
                    status = "pending"
                )
            )
            _applications.value = current
        }
    }

    fun cancelApplication(eventId: String) {
        _applications.value = _applications.value.filter { it.eventId != eventId }
    }

    fun addEvent(event: StubEvent) {
        val current = _events.value.toMutableList()
        current.add(0, event)
        _events.value = current
    }

    fun isApplied(eventId: String): Boolean =
        _applications.value.any { it.eventId == eventId }
}
