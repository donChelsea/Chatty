package com.example.chatty.ui.main.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty.domain.ChatMessage
import com.example.chatty.domain.User
import com.example.chatty.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _state: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private val _chattingEvents = MutableSharedFlow<ChattingEvent>()
    val chattingEvents: SharedFlow<ChattingEvent> = _chattingEvents.asSharedFlow()

    private val fromId = auth.uid

    fun postEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.OnUpdateSelectedUser -> { _state.update { it.copy(toUser = event.user) } }
            is ChatUiEvent.OnSendMessage -> sendMessage(event.text)
            is ChatUiEvent.OnListenForMessages -> listenForMessages()
        }
    }

    private fun sendMessage(text: String) {
        val toId = _state.value.toUser?.uid
        val fromRef = database.getReference("/user-messages/$fromId/$toId").push()
        val toRef = database.getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(
            id = fromRef.key!!,
            text = text,
            fromId = fromId.toString(),
            toId = toId.toString(),
            timeStamp = System.currentTimeMillis() / 1000
        )
        fromRef.setValue(chatMessage)
            .addOnSuccessListener {
                println("message saved to db: ${fromRef.key}")
            }
        toRef.setValue(chatMessage)

        val recentMessageFromUserRef = database.getReference("/recent-messages/$fromId/$toId")
        recentMessageFromUserRef.setValue(chatMessage)

        val recentMessageToUserRef = database.getReference("/recent-messages/$toId/$fromId")
        recentMessageToUserRef.setValue(chatMessage)
    }

    private fun listenForMessages() {
        val toId = _state.value.toUser?.uid

        val ref = database.getReference("/user-messages/$toId/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let { chat ->
                    println("new chat added: ${chat.text}")
                    viewModelScope.launch {
                        if (chat.fromId == auth.uid) {
                            _chattingEvents.emit(ChattingEvent.OnToChat(chat.text, MainActivity.currentUser))
                        } else {
                            _chattingEvents.emit(ChattingEvent.OnFromChat(chat.text, _state.value.toUser))
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

data class ChatUiState(
    val toUser: User? = null,
)

sealed class ChatUiEvent {
    data class OnUpdateSelectedUser(val user: User) : ChatUiEvent()
    data class OnSendMessage(val text: String) : ChatUiEvent()
    object OnListenForMessages : ChatUiEvent()
}

sealed class ChattingEvent {
    data class OnFromChat(val text: String, val fromUser: User?): ChattingEvent()
    data class OnToChat(val text: String, val toUser: User?): ChattingEvent()
}
