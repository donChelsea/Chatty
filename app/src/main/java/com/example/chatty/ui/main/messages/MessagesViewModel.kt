package com.example.chatty.ui.main.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty.domain.ChatMessage
import com.example.chatty.domain.User
import com.example.chatty.ui.main.MainActivity
import com.example.chatty.ui.main.chat.ChattingEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
class MessagesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
) : ViewModel() {

    private val _state: MutableStateFlow<MessagesUiState> = MutableStateFlow(MessagesUiState())
    val state: StateFlow<MessagesUiState> = _state.asStateFlow()

    private val _messageEvents = MutableSharedFlow<MessagesEvent.UpdateMessagesMap>()
    val messageEvents: SharedFlow<MessagesEvent.UpdateMessagesMap> = _messageEvents.asSharedFlow()

    init {
        fetchCurrentUser()
        listenForRecentMessages()
    }

    private fun fetchCurrentUser() {
        val uid = auth.uid
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MainActivity.currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenForRecentMessages() {
        val fromId = auth.uid
        val ref = database.getReference("/recent-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val map = mutableMapOf<String, ChatMessage>()
                map[snapshot.key!!] = chatMessage
                viewModelScope.launch { _messageEvents.emit(MessagesEvent.UpdateMessagesMap(map)) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val map = mutableMapOf<String, ChatMessage>()
                map[snapshot.key!!] = chatMessage
                viewModelScope.launch { _messageEvents.emit(MessagesEvent.UpdateMessagesMap(map)) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

data class MessagesUiState(
    val recentMessage: ChatMessage? = null,
    val recentMessagesMap: Map<String, ChatMessage> = mutableMapOf(),
)

sealed class MessagesEvent {
    data class UpdateMessagesMap(val map: Map<String, ChatMessage>) : MessagesEvent()
}
