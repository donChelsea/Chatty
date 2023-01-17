package com.example.chatty.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.chatty.databinding.ActivityRegisterBinding
import com.example.chatty.login.LoginActivity
import kotlinx.coroutines.flow.collectLatest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            username.doAfterTextChanged {
                if (toString().isNotEmpty()) viewModel.postEvent(RegistrationEvent.UpdateUsername(toString()))
            }

            email.doAfterTextChanged {
                if (toString().isNotEmpty()) viewModel.postEvent(RegistrationEvent.UpdateEmail(toString()))
            }

            password.doAfterTextChanged {
                if (toString().isNotEmpty()) viewModel.postEvent(RegistrationEvent.UpdatePassword(toString()))
            }

            loginTextButton.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            lifecycleScope.launchWhenStarted {
                viewModel.state.collectLatest { state ->
                    register.setOnClickListener {
                        if (state.username.isNullOrEmpty() && state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            Toast.makeText(this@RegisterActivity, "Please fill out an username, email, and/or password.", Toast.LENGTH_SHORT).show()
                        } else {
                            println("User: ${state.username}, ${state.email}, ${state.password}")
                        }
                    }
                }
            }
        }

    }

}