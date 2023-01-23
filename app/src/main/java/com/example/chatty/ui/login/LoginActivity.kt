package com.example.chatty.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.chatty.R
import com.example.chatty.databinding.ActivityLoginBinding
import com.example.chatty.ui.MainActivity
import com.example.chatty.util.isPasswordValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            email.setOnFocusChangeListener { _, hasFocus ->
                val text = email.text.toString()
                if (!hasFocus) {
                    if (text.isNotEmpty()) {
                        viewModel.postEvent(LoginEvent.OnEnterEmail(text.trim()))
                    } else {
                        email.error = getString(R.string.error_enter_email)
                    }
                }
            }

            password.doAfterTextChanged {
                val text = password.text.toString()
                if (text.isNotEmpty() && text.isPasswordValid()) {
                    viewModel.postEvent(LoginEvent.OnEnterPassword(text.trim()))
                } else {
                    password.error = getString(R.string.error_enter_password)
                }
            }


            lifecycleScope.launchWhenStarted {
                viewModel.state.collectLatest { state ->
                    signIn.setOnClickListener {
                        if (state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            return@setOnClickListener
                        } else {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}