package com.example.campusconnect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>
    private lateinit var dbRef: DatabaseReference
    private lateinit var searchField: EditText
    private lateinit var txtEmptySearch: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // FIXED: Find views with correct IDs
        searchField = view.findViewById(R.id.editSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postList = mutableListOf()
        searchAdapter = PostAdapter(postList)
        recyclerView.adapter = searchAdapter

        // Add empty state
        txtEmptySearch = TextView(requireContext()).apply {
            text = "🔍 Search Campus Connect\n\nFind posts, events, and people from your campus!"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        (view as ViewGroup).addView(txtEmptySearch)

        dbRef = FirebaseDatabase.getInstance().getReference("Posts")
        loadPosts()

        // Search functionality
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPosts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun loadPosts() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)
                    if (post != null) postList.add(post)
                }
                searchAdapter.notifyDataSetChanged()

                if (postList.isNotEmpty()) {
                    txtEmptySearch.visibility = View.GONE
                } else {
                    // If no posts in database, create some samples for search
                    createSamplePostsForSearch()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                createSamplePostsForSearch()
            }
        })
    }

    private fun filterPosts(query: String) {
        if (query.isEmpty()) {
            // Show all posts when search is empty
            searchAdapter.updateList(postList)
            txtEmptySearch.visibility = if (postList.isEmpty()) View.VISIBLE else View.GONE
            txtEmptySearch.text = "🔍 Search Campus Connect\n\nFind posts, events, and people from your campus!"
            return
        }

        val filtered = postList.filter {
            it.text?.contains(query, ignoreCase = true) == true ||
                    it.userName?.contains(query, ignoreCase = true) == true
        }

        searchAdapter.updateList(filtered)
        txtEmptySearch.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        txtEmptySearch.text = "No results found for \"$query\"\n\nTry searching for: events, courses, campus"
    }

    private fun createSamplePostsForSearch() {
        val samplePosts = listOf(
            Post(
                postId = "search_sample1",
                userId = "system",
                userName = "Computer Science Dept",
                text = "Programming Workshop this Wednesday in Lab 205. Learn Python basics!"
            ),
            Post(
                postId = "search_sample2",
                userId = "system",
                userName = "Student Union",
                text = "Campus Festival this Friday! Music, food, and games at the main ground."
            ),
            Post(
                postId = "search_sample3",
                userId = "system",
                userName = "Library",
                text = "Extended hours during finals week. Open until midnight!"
            )
        )

        postList.addAll(samplePosts)
        searchAdapter.notifyDataSetChanged()
        txtEmptySearch.visibility = View.GONE

        // Save to database
        samplePosts.forEach { post ->
            dbRef.child(post.postId!!).setValue(post)
        }
    }
}