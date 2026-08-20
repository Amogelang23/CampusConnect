package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var friendsAdapter: FriendsAdapter
    private val friendsList = mutableListOf<Friend>()
    private lateinit var txtEmptyFriends: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriends)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create empty state
        txtEmptyFriends = TextView(requireContext()).apply {
            text = "👥 Campus Friends\n\nYour friends from campus will appear here!\n\nConnect with classmates and build your campus network."
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        (view as ViewGroup).addView(txtEmptyFriends)

        friendsAdapter = FriendsAdapter(friendsList) { friendId ->
            android.widget.Toast.makeText(requireContext(), "Friend removed", android.widget.Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = friendsAdapter

        loadFriends()

        return view
    }

    private fun loadFriends() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val friend = snap.getValue(Friend::class.java)
                        friend?.let { friendsList.add(it) }
                    }
                }

                if (friendsList.isNotEmpty()) {
                    txtEmptyFriends.visibility = View.GONE
                    friendsAdapter.notifyDataSetChanged()
                } else {
                    createSampleFriends()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                createSampleFriends()
            }
        })
    }

    private fun createSampleFriends() {
        val sampleFriends = listOf(
            Friend("friend1", "Alex Johnson", "Computer Science"),
            Friend("friend2", "Sarah Williams", "Engineering"),
            Friend("friend3", "Mike Chen", "Business")
        )

        friendsList.addAll(sampleFriends)
        friendsAdapter.notifyDataSetChanged()
        txtEmptyFriends.visibility = View.GONE
    }
}