package com.example.chatty.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.chatty.R
import com.example.chatty.databinding.ActivityRegisterBinding
import com.example.chatty.ui.login.LoginActivity
import com.example.chatty.util.isPasswordValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            username.setOnFocusChangeListener { _, hasFocus ->
                val text = username.text.toString()
                if (!hasFocus) {
                    if (text.isNotEmpty()) {
                        viewModel.postEvent(RegistrationEvent.OnEnterUsername(text.trim()))
                    } else {
                        username.error = getString(R.string.error_enter_username)
                    }
                }
            }

            email.setOnFocusChangeListener { _, hasFocus ->
                val text = email.text.toString()
                if (!hasFocus) {
                    if (text.isNotEmpty()) {
                        println(text)
                        viewModel.postEvent(RegistrationEvent.OnEnterEmail(text.trim()))
                    } else {
                        email.error = getString(R.string.error_enter_email)
                    }
                }
            }

            password.doAfterTextChanged {
                val text = password.text.toString()
                if (text.isNotEmpty() && text.isPasswordValid()) {
                    viewModel.postEvent(RegistrationEvent.OnEnterPassword(text.trim()))
                } else {
                    password.error = getString(R.string.error_enter_password)
                }
            }

            loginTextButton.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            lifecycleScope.launchWhenStarted {
                viewModel.state.collectLatest { state ->
                    register.setOnClickListener {
                        if (state.username.isNullOrEmpty() && state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            return@setOnClickListener
                        } else {
                            viewModel.postEvent(RegistrationEvent.OnFinishRegistration)
//                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

}