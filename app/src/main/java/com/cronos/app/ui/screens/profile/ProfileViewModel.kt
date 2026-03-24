package com.cronos.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.model.EventApplication
import com.cronos.app.data.model.PortfolioItem
import com.cronos.app.data.model.Profile
import com.cronos.app.data.repository.AppStateRepository
import com.cronos.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: Profile? = null,
    val portfolio: List<PortfolioItem> = emptyList(),
    val myEvents: List<EventApplication> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        // Следим за изменениями заявок в реальном времени
        viewModelScope.launch {
            appStateRepository.applications.collect { apps ->
                _uiState.value = _uiState.value.copy(myEvents = apps)
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            profileRepository.getMyProfile()
                .onSuccess { profile ->
                    val stubPortfolio = listOf(
                        PortfolioItem("1", profile.id, "Мой первый проект", "Описание проекта", null, "https://github.com"),
                        PortfolioItem("2", profile.id, "Хакатон 2025", "Победитель трека IT", null, null),
                    )
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        portfolio = stubPortfolio,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            profileRepository.updateUsername(username)
                .onSuccess { loadProfile() }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun saveAvatarUrl(url: String) {
        viewModelScope.launch {
            profileRepository.updateAvatarUrl(url)
                .onSuccess { loadProfile() }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun addPortfolioItem(title: String, description: String, projectUrl: String) {
        viewModelScope.launch {
            profileRepository.addPortfolioItem(title, description, projectUrl)
                .onSuccess { loadProfile() }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }
}
