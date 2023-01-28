package com.example.chatty.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.chatty.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun postEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEnterEmail -> _state.update { it.copy(email = event.email) }
            is LoginEvent.OnEnterPassword -> _state.update { it.copy(password = event.password) }
            is LoginEvent.FinishLogin -> login(event.email, event.password)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.update { it.copy(userLoggedIn = true) }
                    println("signed in as: ${task.result.user?.email}")
                } else {
                    println("couldn't sign in: ${task.exception}")
                }
            }
    }
}

sealed class LoginEvent {
    data class OnEnterEmail(val email: String) : LoginEvent()
    data class OnEnterPassword(val password: String) : LoginEvent()
    class FinishLogin(val email: String, val password: String) : LoginEvent()
}

data class LoginUiState(
    val email: String = StringUtils.EMPTY,
    val password: String = StringUtils.EMPTY,
    val userLoggedIn: Boolean = false
)