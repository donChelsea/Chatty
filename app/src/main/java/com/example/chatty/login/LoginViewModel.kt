package com.example.chatty.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _state: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun postEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> _state.update { it.copy(email = event.email) }
            is LoginEvent.UpdatePassword -> _state.update { it.copy(email = event.password) }
        }
    }
}

sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
}

data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
)