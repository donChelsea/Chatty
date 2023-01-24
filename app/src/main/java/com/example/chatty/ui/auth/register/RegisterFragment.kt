package com.example.chatty.ui.auth.register

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.chatty.R
import com.example.chatty.databinding.FragmentRegisterBinding
import com.example.chatty.ui.main.MainActivity
import com.example.chatty.util.isPasswordValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    private val getImageContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            viewModel.postEvent(RegistrationEvent.OnSelectProfileImage(uri))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.register)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            selectPhoto.setOnClickListener {
                getImageContent.launch(arrayOf("image/*"))
                setPhotoBackground()
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
                view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            lifecycleScope.launchWhenStarted {
                viewModel.state.collectLatest { state ->
                    register.setOnClickListener {
                        if (state.username.isNullOrEmpty() && state.email.isNullOrEmpty() && state.password.isNullOrEmpty()) {
                            return@setOnClickListener
                        } else {
                            viewModel.postEvent(RegistrationEvent.OnFinishRegistration)
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setPhotoBackground() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                state.imageUri?.let {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(requireActivity().contentResolver, state.imageUri)
                    requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val bitmap = source.let { data -> ImageDecoder.decodeBitmap(data) }
                    binding.apply {
                        circleImageview.setImageBitmap(bitmap)
                        selectPhoto.alpha = 0f
                    }
                }
            }
        }
    }
}