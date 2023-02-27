package com.example.chatty.ui.main.new_message

import androidx.lifecycle.ViewModel
import com.example.chatty.domain.User
import com.example.chatty.ui.main.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val database: FirebaseDatabase,
) : ViewModel() {

    private val _state: MutableStateFlow<NewMessagesUiState> = MutableStateFlow(NewMessagesUiState())
    val state: StateFlow<NewMessagesUiState> = _state

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = database.getReference("/users")
        val users = mutableListOf<User>()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { data ->
                    val user = data.getValue(User::class.java)
                    if (user?.uid != MainActivity.currentUser?.uid) {
                        user?.let { it -> users.add(it) }
                    }
                }
                _state.update { it.copy(users = users) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}

data class NewMessagesUiState(
    val users: List<User> = emptyList()
)