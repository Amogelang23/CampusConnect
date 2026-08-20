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

class SendFriendRequestFragment : Fragment() {

    private lateinit var editEmail: EditText
    private lateinit var btnSendRequest: Button
    private val db = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send_friend_request, container, false)

        editEmail = view.findViewById(R.id.editFriendEmail)
        btnSendRequest = view.findViewById(R.id.btnSendRequest)

        btnSendRequest.setOnClickListener {
            val email = editEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendFriendRequest(email)
            }
        }

        return view
    }

    private fun sendFriendRequest(email: String) {
        db.child("Users").orderByChild("email").equalTo(email)
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userId = snapshot.children.first().key
                    val userName = snapshot.children.first().child("name").value as? String
                    val userProfile = snapshot.children.first().child("profileImage").value as? String

                    val request = FriendRequest(
                        fromUid = currentUser?.uid,
                        fromName = currentUser?.displayName ?: "Unknown",
                        fromProfile = currentUser?.photoUrl?.toString()
                    )

                    userId?.let {
                        db.child("FriendRequests").child(it).push().setValue(request)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Failed to send request.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
