package com.example.chatty.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatty.R
import com.example.chatty.databinding.FragmentLoginBinding
import com.example.chatty.ui.main.MainActivity
import com.example.chatty.util.isPasswordValid
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.sign_in)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                viewModel.state.collect { state ->
                    signIn.setOnClickListener {
                        if (state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            return@setOnClickListener
                        } else {
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}