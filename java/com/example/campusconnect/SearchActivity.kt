package com.example.campusconnect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {

    private lateinit var searchField: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: PostAdapter
    private lateinit var txtEmpty: TextView
    private val searchResults = ArrayList<Post>()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // FIXED: Use activity_search layout

        searchField = findViewById(R.id.searchField)
        recyclerView = findViewById(R.id.recyclerSearch)

        // Add empty state
        txtEmpty = TextView(this).apply {
            text = "🔍 Search Campus Connect\n\nFind posts, events, and people!"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        (recyclerView.parent as? android.view.ViewGroup)?.addView(txtEmpty)

        searchAdapter = PostAdapter(searchResults)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            searchResults.clear()
            searchAdapter.notifyDataSetChanged()
            txtEmpty.text = "🔍 Search Campus Connect\n\nFind posts, events, and people!"
            txtEmpty.visibility = View.VISIBLE
            return
        }

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchResults.clear()

                for (snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)
                    post?.let {
                        val matchesText = it.text?.contains(query, ignoreCase = true) == true
                        val matchesUser = it.userName?.contains(query, ignoreCase = true) == true

                        if (matchesText || matchesUser) {
                            searchResults.add(it)
                        }
                    }
                }

                searchAdapter.notifyDataSetChanged()

                if (searchResults.isEmpty()) {
                    txtEmpty.text = "No results found for \"$query\""
                    txtEmpty.visibility = View.VISIBLE
                } else {
                    txtEmpty.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                txtEmpty.text = "Search failed. Please try again."
                txtEmpty.visibility = View.VISIBLE
            }
        })
    }
}