package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(
    private val posts: MutableList<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.txtUserName.text = post.userName ?: "Unknown User"
        holder.txtPostText.text = post.text ?: ""
        holder.txtLikes.text = "${post.likes} Likes"

        // Load profile image
        if (!post.userProfile.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.userProfile)
                .circleCrop()
                .into(holder.imgProfile)
        } else {
            holder.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        // Load post image if available
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.imgPost.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.imageUrl)
                .into(holder.imgPost)
        } else {
            holder.imgPost.visibility = View.GONE
        }

        // Like button functionality
        holder.btnLike.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Liked post!", Toast.LENGTH_SHORT).show()
        }

        // Comment button functionality
        holder.btnComment.setOnClickListener {
            val commentText = holder.editComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                Toast.makeText(holder.itemView.context, "Comment: $commentText", Toast.LENGTH_SHORT).show()
                holder.editComment.text.clear()
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    // ADD THIS MISSING METHOD
    fun updateList(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgUserProfile)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtPostText: TextView = itemView.findViewById(R.id.txtPostText)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val btnLike: Button = itemView.findViewById(R.id.btnLike)
        val txtLikes: TextView = itemView.findViewById(R.id.txtLikes)
        val btnComment: Button = itemView.findViewById(R.id.btnComment)
        val editComment: EditText = itemView.findViewById(R.id.editComment)
        val txtComments: TextView = itemView.findViewById(R.id.txtComments)
    }
}