package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var txtEmpty: TextView
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPosts)

        // Create empty state
        txtEmpty = TextView(requireContext()).apply {
            text = "Loading campus posts... 🎓"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        (view as android.view.ViewGroup).addView(txtEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        loadPosts()

        return view
    }

    private fun loadPosts() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Posts")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val post = snap.getValue(Post::class.java)
                        post?.let {
                            postList.add(0, it) // Newest first
                        }
                    }
                }

                if (postList.isNotEmpty()) {
                    txtEmpty.visibility = View.GONE
                    postAdapter.notifyDataSetChanged()
                } else {
                    createSamplePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                createSamplePosts()
            }
        })
    }

    private fun createSamplePosts() {
        val samplePosts = listOf(
            Post(
                postId = "sample1",
                userId = "system",
                userName = "Campus Administration",
                text = "🎓 Welcome to Campus Connect! Share your campus experiences and connect with friends!"
            ),
            Post(
                postId = "sample2",
                userId = "system",
                userName = "Student Union",
                text = "🎉 Campus Festival this Friday! Join us for music and fun!"
            )
        )

        postList.addAll(samplePosts)
        postAdapter.notifyDataSetChanged()
        txtEmpty.visibility = View.GONE
    }
}