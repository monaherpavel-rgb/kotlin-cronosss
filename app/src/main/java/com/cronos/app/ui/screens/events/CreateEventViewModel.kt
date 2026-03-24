package com.cronos.app.ui.screens.events

import androidx.lifecycle.ViewModel
import com.cronos.app.data.repository.AppStateRepository
import com.cronos.app.data.stub.StubEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val appState: AppStateRepository
) : ViewModel() {

    fun saveEvent(
        title: String,
        description: String,
        date: String,
        format: String,
        direction: String,
        difficulty: Int,
        maxParticipants: Int,
        points: Int
    ) {
        appState.addEvent(
            StubEvent(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                date = date,
                format = format,
                direction = direction,
                difficulty = difficulty,
                maxParticipants = maxParticipants,
                currentParticipants = 0,
                points = points,
                organizerName = "Вы",
                reward = "$points баллов"
            )
        )
    }
}
