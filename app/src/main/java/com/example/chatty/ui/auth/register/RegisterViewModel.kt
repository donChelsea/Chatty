package com.example.chatty.ui.auth.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.chatty.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase,
) : ViewModel() {

    private val _state: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun postEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnEnterUsername -> _state.update { it.copy(username = event.username) }
            is RegistrationEvent.OnEnterEmail -> _state.update { it.copy(email = event.email) }
            is RegistrationEvent.OnEnterPassword -> _state.update { it.copy(password = event.password) }
            is RegistrationEvent.OnSelectProfileImage -> _state.update { it.copy(imageUri = event.uri) }
            is RegistrationEvent.OnFinishRegistration -> {
                auth.createUserWithEmailAndPassword(_state.value.email.toString(), _state.value.password.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _state.update { it.copy(userCreated = true) }
                            uploadImage()
                            println("User created: ${task.result.user?.uid}")
                            return@addOnCompleteListener
                        }
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(userCreated = false) }
                        println("User couldn't be created: ${e.message}")
                    }
            }
        }
    }

    private fun uploadImage() {
        if (_state.value.imageUri == null) return

        val fileName = UUID.randomUUID().toString()
        val reference = storage.getReference("/images/$fileName")
        reference.putFile(_state.value.imageUri!!)
            .addOnSuccessListener { task ->
                println("Added image: ${task.metadata?.path}")
                reference.downloadUrl.addOnSuccessListener { url ->
                    println("File location: $url")
                    saveUserToDatabase(url)
                }
            }
    }

    private fun saveUserToDatabase(url: Uri) {
        val uid = auth.uid.orEmpty()
        val ref = database.getReference("/users/$uid")
        val user = User(uid, _state.value.username.orEmpty(), url.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                println("user saved to db: ${user.uid}")
            }
            .addOnFailureListener {
                println("user couldn't be saved to db: ${user.uid}")
            }
    }
}

sealed class RegistrationEvent {
    data class OnEnterUsername(val username: String) : RegistrationEvent()
    data class OnEnterEmail(val email: String) : RegistrationEvent()
    data class OnEnterPassword(val password: String) : RegistrationEvent()
    data class OnSelectProfileImage(val uri: Uri?) : RegistrationEvent()
    object OnFinishRegistration : RegistrationEvent()
}

data class RegisterUiState(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val imageUri: Uri? = null,
    val userCreated: Boolean? = null,
)