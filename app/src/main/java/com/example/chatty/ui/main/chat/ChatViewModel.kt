package com.example.chatty.ui.main.chat

import androidx.lifecycle.ViewModel
import com.example.chatty.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state

    fun postEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.UpdateState -> _state.update { it.copy(user = event.user) }
        }
    }
}

data class ChatUiState(
    val user: User? = null
)

sealed class ChatEvent {
    data class UpdateState(val user: User): ChatEvent()
}