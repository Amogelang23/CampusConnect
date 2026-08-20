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

class AcceptFriendRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendRequestAdapter
    private val requestList = mutableListOf<FriendRequest>()

    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accept_friend_requests, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewRequests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FriendRequestAdapter(requestList, ::acceptRequest)
        recyclerView.adapter = adapter

        db = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid

        loadRequests()

        return view
    }

    private fun loadRequests() {
        currentUserId?.let { uid ->
            db.child("FriendRequests").child(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        requestList.clear()
                        for (snap in snapshot.children) {
                            val req = snap.getValue(FriendRequest::class.java)
                            req?.let { requestList.add(it) }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load requests", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun acceptRequest(request: FriendRequest) {
        currentUserId?.let { uid ->
            request.fromUid?.let { friendUid ->
                // Add each other as friends
                db.child("Friends").child(uid).child(friendUid).setValue(true)
                db.child("Friends").child(friendUid).child(uid).setValue(true)

                // Remove the friend request
                db.child("FriendRequests").child(uid).child(friendUid).removeValue()
                Toast.makeText(context, "Friend added!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
