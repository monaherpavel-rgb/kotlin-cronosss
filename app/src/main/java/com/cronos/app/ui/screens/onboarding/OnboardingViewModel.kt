package com.cronos.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val isDone: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun saveParticipant(firstName: String, lastName: String, birthDate: String, city: String, interests: List<String>, motivation: String) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState(isLoading = true)
            profileRepository.saveParticipantOnboarding(firstName, lastName, birthDate, city, interests, motivation)
                .onSuccess { _uiState.value = OnboardingUiState(isDone = true) }
                .onFailure { _uiState.value = OnboardingUiState(error = it.message) }
        }
    }

    fun saveOrganizer(firstName: String, organization: String, position: String, eventTypes: List<String>) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState(isLoading = true)
            profileRepository.saveOrganizerOnboarding(firstName, organization, position, eventTypes)
                .onSuccess { _uiState.value = OnboardingUiState(isDone = true) }
                .onFailure { _uiState.value = OnboardingUiState(error = it.message) }
        }
    }

    fun saveObserver(firstName: String, organization: String, observerRole: String) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState(isLoading = true)
            profileRepository.saveObserverOnboarding(firstName, organization, observerRole)
                .onSuccess { _uiState.value = OnboardingUiState(isDone = true) }
                .onFailure { _uiState.value = OnboardingUiState(error = it.message) }
        }
    }
}
