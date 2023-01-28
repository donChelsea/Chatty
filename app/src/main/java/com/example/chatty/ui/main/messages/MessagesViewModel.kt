package com.example.chatty.ui.main.messages

import androidx.lifecycle.ViewModel
import com.example.chatty.domain.User
import com.example.chatty.ui.main.MainActivity
import com.example.chatty.ui.main.chat.ChatUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
) : ViewModel() {

    private val _state: MutableStateFlow<MessagesUiState> = MutableStateFlow(MessagesUiState())
    val state: StateFlow<MessagesUiState> = _state.asStateFlow()

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        val uid = auth.uid
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MainActivity.currentUser = snapshot.getValue(User::class.java)
                _state.update { it.copy(currentUser = MainActivity.currentUser) }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}

data class MessagesUiState(
    val currentUser: User? = null,
)
