package com.example.chatty.ui.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.chatty.R
import com.example.chatty.databinding.FragmentChatBinding
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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.user.username
        viewModel.postEvent(ChatEvent.UpdateState(args.user))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter<GroupieViewHolder>()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->


            }
        }

        binding.recyclerview.adapter = adapter
    }

}

class ChatFromItem: Item<GroupieViewHolder>() {
    override fun bind(p0: GroupieViewHolder, p1: Int) {

    }

    override fun getLayout() = R.layout.list_item_chat_from
}

class ChatToItem: Item<GroupieViewHolder>() {
    override fun bind(p0: GroupieViewHolder, p1: Int) {

    }

    override fun getLayout() = R.layout.list_item_chat_to
}