package com.cronos.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cronos.app.data.repository.AppStateRepository
import com.cronos.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ADMIN_EMAIL = "admin@gmail.com"
private const val ADMIN_PASSWORD = "admin123"

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        if (appStateRepository.isAdminMode.value || authRepository.isUserLoggedIn()) {
            _uiState.value = _uiState.value.copy(isLoggedIn = true)
        }
    }

    fun signIn(email: String, password: String) {
        // Локальный обход для администратора — без Supabase
        if (email.trim() == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            appStateRepository.setAdminMode(true)
            _uiState.value = _uiState.value.copy(isLoggedIn = true)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signInWithEmail(email, password)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message ?: "Ошибка входа") }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signUpWithEmail(email, password)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, isRegistered = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message ?: "Ошибка регистрации") }
        }
    }

    fun signInWithGoogle() { /* requires deeplink config */ }
}
