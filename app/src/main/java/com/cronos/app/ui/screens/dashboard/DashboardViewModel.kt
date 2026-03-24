package com.cronos.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.model.Profile
import com.cronos.app.data.repository.AppStateRepository
import com.cronos.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true
)

private val ADMIN_PROFILE = Profile(
    id = "admin",
    email = "admin@gmail.com",
    firstName = "Администратор",
    role = "admin",
    onboardingCompleted = true,
    verificationStatus = "approved"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        // Если admin-режим — не идём в Supabase
        if (appStateRepository.isAdminMode.value) {
            _uiState.value = DashboardUiState(profile = ADMIN_PROFILE, isLoading = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            profileRepository.getMyProfile()
                .onSuccess { profile -> _uiState.value = DashboardUiState(profile = profile, isLoading = false) }
                .onFailure { _uiState.value = DashboardUiState(isLoading = false) }
        }
    }
}
