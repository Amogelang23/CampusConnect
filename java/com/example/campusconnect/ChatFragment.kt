package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: MutableList<ChatMessage>
    private lateinit var txtEmptyChat: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        chatList = mutableListOf()

        txtEmptyChat = TextView(requireContext()).apply {
            text = "Start a conversation! 💬"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        (view as android.view.ViewGroup).addView(txtEmptyChat)

        recyclerView = view.findViewById(R.id.recyclerViewChat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(chatList)
        recyclerView.adapter = chatAdapter

        loadMessages()

        val editMessage = view.findViewById<EditText>(R.id.editMessage)
        val btnSend = view.findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val msg = editMessage.text.toString().trim()
            if (msg.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val chatMsg = ChatMessage(
                    senderId = currentUser?.uid ?: "user",
                    receiverId = "all",
                    message = msg,
                    timestamp = System.currentTimeMillis()
                )
                FirebaseDatabase.getInstance().getReference("Chats").push().setValue(chatMsg)
                editMessage.text.clear()
                txtEmptyChat.visibility = View.GONE
            }
        }

        return view
    }

    private fun loadMessages() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Chats")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (snap in snapshot.children) {
                    val chat = snap.getValue(ChatMessage::class.java)
                    chat?.let { chatList.add(it) }
                }
                chatAdapter.notifyDataSetChanged()

                if (chatList.isNotEmpty()) {
                    txtEmptyChat.visibility = View.GONE
                    recyclerView.scrollToPosition(chatList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}