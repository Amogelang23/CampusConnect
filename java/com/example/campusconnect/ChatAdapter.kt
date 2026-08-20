package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Use a simple layout for all messages
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_simple, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtMessage.text = chat.message
        holder.txtSender.text = if (chat.senderId == currentUser) "You" else "Friend"

        // Simple styling - you sent messages on right, received on left
        if (chat.senderId == currentUser) {
            holder.txtMessage.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light))
        } else {
            holder.txtMessage.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
        }
    }

    override fun getItemCount(): Int = chatList.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        val txtSender: TextView = itemView.findViewById(R.id.txtSender)
    }
}