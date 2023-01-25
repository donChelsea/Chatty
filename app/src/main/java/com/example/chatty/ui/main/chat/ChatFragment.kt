package com.example.chatty.ui.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatty.R
import com.example.chatty.databinding.FragmentChatBinding
import com.example.chatty.util.hideKeyboard
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()
    private val args by navArgs<ChatFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel.postEvent(ChatUiEvent.OnUpdateSelectedUser(args.user))
        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.user.username
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.postEvent(ChatUiEvent.OnListenForMessages)

        val adapter = GroupAdapter<GroupieViewHolder>()
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true

        binding.apply {
            recyclerview.apply {
                this.adapter = adapter
                this.layoutManager = layoutManager
            }
            send.setOnClickListener {
                viewModel.postEvent(ChatUiEvent.OnSendMessage(binding.message.text.toString()))
                message.text.clear()
                recyclerview.scrollToPosition(adapter.itemCount - 1)
                it.hideKeyboard()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.chattingEvents.collect { event ->
                when (event) {
                    is ChattingEvent.OnFromChat -> adapter.add(ChatFromItem(event.text))
                    is ChattingEvent.OnToChat -> adapter.add(ChatToItem(event.text))
                }
            }
        }
    }
}

class ChatFromItem(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message).text = text
    }

    override fun getLayout() = R.layout.list_item_chat_from
}

class ChatToItem(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message).text = text
    }

    override fun getLayout() = R.layout.list_item_chat_to
}