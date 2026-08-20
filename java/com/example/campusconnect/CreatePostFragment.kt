package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreatePostFragment : Fragment() {

    private lateinit var editPostText: EditText
    private lateinit var btnPost: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        editPostText = view.findViewById(R.id.editPost)
        btnPost = view.findViewById(R.id.btnUpload)

        btnPost.setOnClickListener {
            createPost()
        }

        return view
    }

    private fun createPost() {
        val text = editPostText.text.toString().trim()
        val user = FirebaseAuth.getInstance().currentUser

        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Please write something to post", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = FirebaseDatabase.getInstance().getReference("Posts").push().key ?: return

        val post = Post(
            postId = postId,
            userId = user?.uid ?: "anonymous",
            userName = user?.displayName ?: "Anonymous User",
            text = text,
            timestamp = System.currentTimeMillis(),
            likes = 0
        )

        FirebaseDatabase.getInstance().getReference("Posts").child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show()
                editPostText.text.clear()

                // ALTERNATIVE: Navigate using bottom navigation directly
                navigateToHome()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHome() {
        // Method 1: Use the activity's bottom navigation
        (activity as? HomeActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            ?.selectedItemId = R.id.navHome

        // Method 2: Direct fragment transaction (more reliable)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }
}