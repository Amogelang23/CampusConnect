package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var friendsList: MutableList<Friend>
    private lateinit var dbRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriends)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        friendsList = mutableListOf()
        // Pass remove callback lambda
        friendsAdapter = FriendsAdapter(friendsList) { friendId ->
            removeFriend(friendId)
        }
        recyclerView.adapter = friendsAdapter

        dbRef = FirebaseDatabase.getInstance().getReference("Friends/$currentUser")
        loadFriends()

        return view
    }

    private fun loadFriends() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()
                for (snap in snapshot.children) {
                    val friend = snap.getValue(Friend::class.java)
                    friend?.let { friendsList.add(it) }
                }
                friendsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load friends", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeFriend(friendId: String) {
        currentUser?.let { uid ->
            dbRef.child(friendId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to remove friend", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
