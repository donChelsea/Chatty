package com.example.chatty.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.chatty.R
import com.example.chatty.databinding.ActivityLoginBinding
import com.example.chatty.databinding.ActivityRegisterBinding
import com.example.chatty.register.RegisterViewModel
import com.example.chatty.register.RegistrationEvent
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            email.doAfterTextChanged {
                if (toString().isNotEmpty()) viewModel.postEvent(LoginEvent.UpdateEmail(toString()))
            }

            password.doAfterTextChanged {
                if (toString().isNotEmpty()) viewModel.postEvent(LoginEvent.UpdatePassword(toString()))
            }

            lifecycleScope.launchWhenStarted {
                viewModel.state.collectLatest { state ->
                    signIn.setOnClickListener {
                        if (state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            println("Empty")
                            Toast.makeText(this@LoginActivity, "Please fill out an email and/or password.", Toast.LENGTH_SHORT).show()
                        } else {
                            println("User: ${state.email}, ${state.password}")
                        }
                    }
                }
            }
        }
    }
}