package com.example.chatty.ui.main.new_message

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
import com.example.chatty.databinding.FragmentNewMessageBinding
import com.example.chatty.domain.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewMessageFragment : Fragment() {
    private lateinit var binding: FragmentNewMessageBinding
    private val viewModel by viewModels<NewMessageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewMessageBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.select_user)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter<GroupieViewHolder>()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                state.users.forEach { user ->
                    adapter.add(UserItem(user))
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val action = NewMessageFragmentDirections.actionNewMessageFragmentToChatFragment(userItem.user)
                    view.findNavController().navigate(action)
                }

                binding.recyclerview.adapter = adapter
            }
        }
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.user_name).text = user.username
        Picasso.get().load(user.imageUri).into(viewHolder.itemView.findViewById<ImageView>(R.id.user_image_circle))
    }

    override fun getLayout() = R.layout.list_item_select_user
}