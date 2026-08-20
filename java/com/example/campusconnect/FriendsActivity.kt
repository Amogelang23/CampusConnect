package com.example.campusconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchFriendField: EditText
    private lateinit var btnAddFriend: Button

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter // Fixed: Use FriendsAdapter

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        recyclerView = findViewById(R.id.recyclerFriends)
        searchFriendField = findViewById(R.id.searchFriendField)
        btnAddFriend = findViewById(R.id.btnAddFriend)

        friendsList = ArrayList()

        // FIXED: Explicitly specify the type for the lambda parameter
        friendsAdapter = FriendsAdapter(friendsList) { friendId ->
            // Remove friend callback
            removeFriend(friendId)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = friendsAdapter

        loadFriends()

        btnAddFriend.setOnClickListener {
            val email = searchFriendField.text.toString().trim()
            if (email.isNotEmpty()) {
                addFriend(email)
            } else {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFriends() {
        val userId = auth.currentUser?.uid ?: return

        dbRef.child("Friends").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear()
                    for (snap in snapshot.children) {
                        val friend = snap.getValue(Friend::class.java)
                        friend?.let { friendsList.add(it) }
                    }
                    friendsAdapter.notifyDataSetChanged()

                    if (friendsList.isEmpty()) {
                        showSampleFriends()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FriendsActivity, "Failed to load friends", Toast.LENGTH_SHORT).show()
                    showSampleFriends()
                }
            })
    }

    private fun addFriend(email: String) {
        dbRef.child("Users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@FriendsActivity, "User not found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val currentUserId = auth.currentUser?.uid ?: return
                    val friendSnapshot = snapshot.children.first()
                    val friendId = friendSnapshot.key
                    val friendData = friendSnapshot.getValue(User::class.java)

                    friendId?.let { fid ->
                        val friend = Friend(
                            uid = fid,
                            name = friendData?.name ?: "Unknown User",
                            profileImage = friendData?.profileImage,
                            email = friendData?.email
                        )

                        dbRef.child("Friends").child(currentUserId).child(fid).setValue(friend)
                            .addOnSuccessListener {
                                Toast.makeText(this@FriendsActivity, "Friend added!", Toast.LENGTH_SHORT).show()
                                searchFriendField.text.clear()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@FriendsActivity, "Failed to add friend", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FriendsActivity, "Search failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showSampleFriends() {
        val sampleFriends = listOf(
            Friend("friend1", "Alex Johnson", "alex@campus.edu"),
            Friend("friend2", "Sarah Williams", "sarah@campus.edu"),
            Friend("friend3", "Mike Chen", "mike@campus.edu")
        )
        friendsList.addAll(sampleFriends)
        friendsAdapter.notifyDataSetChanged()
    }

    private fun removeFriend(friendId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        dbRef.child("Friends").child(currentUserId).child(friendId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Friend removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove friend", Toast.LENGTH_SHORT).show()
            }
    }
}