package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FriendsAdapter(
    private val friends: List<Friend>,
    private val removeCallback: (String) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.txtName.text = friend.name ?: "Unknown Friend"

        if (!friend.profileImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(friend.profileImage)
                .circleCrop()
                .into(holder.imgProfile)
        } else {
            holder.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        holder.btnRemove.setOnClickListener {
            friend.uid?.let { removeCallback(it) }
        }
    }

    override fun getItemCount(): Int = friends.size

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgFriendProfile)
        val txtName: TextView = itemView.findViewById(R.id.txtFriendName)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveFriend)
    }
}