package com.example.chatty.ui.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun postEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnEnterUsername -> _state.update { it.copy(username = event.username) }
            is RegistrationEvent.OnEnterEmail -> _state.update { it.copy(email = event.email) }
            is RegistrationEvent.OnEnterPassword -> _state.update { it.copy(password = event.password) }
            is RegistrationEvent.OnFinishRegistration -> {
                auth.createUserWithEmailAndPassword(state.value.email.toString(), state.value.password.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("User created: ${it.result.user.toString()}")
                        return@addOnCompleteListener
                    }
                }
            }
        }
    }
}

sealed class RegistrationEvent {
    data class OnEnterUsername(val username: String) : RegistrationEvent()
    data class OnEnterEmail(val email: String) : RegistrationEvent()
    data class OnEnterPassword(val password: String) : RegistrationEvent()
    object OnFinishRegistration: RegistrationEvent()
}

data class RegisterUiState(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
)