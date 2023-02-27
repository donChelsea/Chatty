package com.example.chatty.ui.main.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chatty.R
import com.example.chatty.databinding.FragmentMessagesBinding
import com.example.chatty.domain.ChatMessage
import com.example.chatty.domain.User
import com.example.chatty.ui.main.MainActivity
import com.example.chatty.ui.main.new_message.UserItem
import com.example.chatty.util.startAnimation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    @Inject
    lateinit var database: FirebaseDatabase

    private lateinit var binding: FragmentMessagesBinding

    private val viewModel by viewModels<MessagesViewModel>()
    private val groupieAdapter = GroupAdapter<GroupieViewHolder>()

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

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
        }

        binding.apply {
            newMessageFab.apply {
                newMessageFab.setOnClickListener {
                    visibility = View.INVISIBLE
                    biggerCircle.visibility = View.VISIBLE
                    biggerCircle.startAnimation(animation) {
                        root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                        biggerCircle.visibility = View.INVISIBLE
                        view.findNavController().navigate(R.id.action_messagesFragment_to_newMessageFragment)
                    }
                }
            }
        }

        refreshMessages()

        groupieAdapter.setOnItemClickListener { item, view ->
            val messageItem = item as RecentMessageItem
            messageItem.chatPartner?.let {
                val action = MessagesFragmentDirections.actionMessagesFragmentToChatFragment(it)
                view.findNavController().navigate(action)
            }
        }

        binding.apply {
            recyclerview.apply {
                adapter = groupieAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun refreshMessages() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageEvents.collect { event ->
                event.map.values.forEach { message ->
                    groupieAdapter.add(RecentMessageItem(message))
                }
            }
        }
    }

    inner class RecentMessageItem(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
        var chatPartner: User? = null

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val usernameTextview = viewHolder.itemView.findViewById<TextView>(R.id.username)
            val previewTextview = viewHolder.itemView.findViewById<TextView>(R.id.preview)
            val imageview = viewHolder.itemView.findViewById<ImageView>(R.id.circle_imageview)

            previewTextview.text = chatMessage.text

            val chatPartnerId: String = if (chatMessage.fromId == MainActivity.currentUser?.uid) {
                chatMessage.toId
            } else {
                chatMessage.fromId
            }
            val ref = database.getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartner = snapshot.getValue(User::class.java)
                    usernameTextview.text = chatPartner?.username
                    Picasso.get().load(chatPartner?.imageUri).placeholder(R.mipmap.ic_launcher).into(imageview)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        override fun getLayout() = R.layout.list_item_message
    }
}

