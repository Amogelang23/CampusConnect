package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FriendRequestAdapter(
    private val requestList: List<FriendRequest>,
    private val onAcceptClicked: (FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val profilePic: ImageView = view.findViewById(R.id.imageProfile)
        val acceptBtn: Button = view.findViewById(R.id.btnAccept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestList[position]
        holder.name.text = request.fromName

        Glide.with(holder.itemView.context)
            .load(request.fromProfile)
            .placeholder(R.mipmap.ic_profile_round)
            .into(holder.profilePic)

        holder.acceptBtn.setOnClickListener {
            onAcceptClicked(request)
        }
    }

    override fun getItemCount(): Int = requestList.size
}
