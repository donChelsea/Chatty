package com.example.chatty.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun postEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEnterEmail -> _state.update { it.copy(email = event.email) }
            is LoginEvent.OnEnterPassword -> _state.update { it.copy(password = event.password) }
        }
    }
}

sealed class LoginEvent {
    data class OnEnterEmail(val email: String) : LoginEvent()
    data class OnEnterPassword(val password: String) : LoginEvent()
}

data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
)