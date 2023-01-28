package com.example.chatty.ui.main.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.chatty.R
import com.example.chatty.databinding.FragmentMessagesBinding
import com.example.chatty.domain.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding

    private val viewModel by viewModels<MessagesViewModel>()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.messages)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshMessages()

        binding.apply {
            recyclerview.adapter = adapter

            newMessageFab.setOnClickListener {
                view.findNavController().navigate(R.id.action_messagesFragment_to_newMessageFragment)
            }
        }
    }

    private fun refreshMessages() {
        adapter.clear()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageEvents.collect { event ->
                event.map.values.forEach { message ->
                    adapter.add(RecentMessageItem(message))
                }
            }
        }
    }
}

class RecentMessageItem(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val usernameTextview = viewHolder.itemView.findViewById<TextView>(R.id.username)
        val previewTextview = viewHolder.itemView.findViewById<TextView>(R.id.preview)
        val imageview = viewHolder.itemView.findViewById<ImageView>(R.id.circle_imageview)

        previewTextview.text = chatMessage.text
    }

    override fun getLayout() = R.layout.list_item_message
}