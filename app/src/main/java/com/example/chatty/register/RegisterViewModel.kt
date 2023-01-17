package com.example.chatty.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _state: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun postEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UpdateUsername -> _state.update { it.copy(email = event.username) }
            is RegistrationEvent.UpdateEmail -> _state.update { it.copy(email = event.email) }
            is RegistrationEvent.UpdatePassword -> _state.update { it.copy(email = event.password) }
        }
    }
}

sealed class RegistrationEvent {
    data class UpdateUsername(val username: String) : RegistrationEvent()
    data class UpdateEmail(val email: String) : RegistrationEvent()
    data class UpdatePassword(val password: String) : RegistrationEvent()
}

data class RegisterUiState(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
)