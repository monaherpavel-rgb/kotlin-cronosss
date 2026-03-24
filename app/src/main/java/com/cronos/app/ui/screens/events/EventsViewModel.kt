package com.cronos.app.ui.screens.events

import androidx.lifecycle.ViewModel
import com.cronos.app.data.repository.AppStateRepository
import com.cronos.app.data.stub.StubEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    val appState: AppStateRepository
) : ViewModel() {

    val events = appState.events
    val applications = appState.applications

    fun apply(event: StubEvent) = appState.applyToEvent(event)
}
