package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var txtEmail: TextView
    private lateinit var txtPostCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnLogout: Button
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val userPosts = ArrayList<Post>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        imgProfile = findViewById(R.id.imgProfile)
        txtEmail = findViewById(R.id.txtEmail)
        txtPostCount = findViewById(R.id.txtPostCount)
        recyclerView = findViewById(R.id.recyclerProfile)
        btnLogout = findViewById(R.id.btnLogout)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // FIXED: Remove the context parameter
        postAdapter = PostAdapter(userPosts)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = postAdapter

        loadUserData()
        loadUserPosts()

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            txtEmail.text = user.email
            // Use a default profile image
            imgProfile.setImageResource(android.R.drawable.sym_def_app_icon)
        }
    }

    private fun loadUserPosts() {
        val userId = auth.currentUser?.uid ?: return
        val userPostsRef = db.getReference("Posts")

        userPostsRef.orderByChild("userId").equalTo(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                userPosts.clear()
                for (snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)
                    post?.let { userPosts.add(it) }
                }
                txtPostCount.text = "${userPosts.size} posts"
                postAdapter.notifyDataSetChanged()

                // If no posts, show sample posts
                if (userPosts.isEmpty()) {
                    showSamplePosts()
                }
            }
    }

    private fun showSamplePosts() {
        val samplePosts = listOf(
            Post(
                postId = "profile_sample1",
                userId = auth.currentUser?.uid ?: "user",
                userName = auth.currentUser?.displayName ?: "You",
                text = "My campus journey begins! 🎓"
            ),
            Post(
                postId = "profile_sample2",
                userId = auth.currentUser?.uid ?: "user",
                userName = auth.currentUser?.displayName ?: "You",
                text = "Beautiful day on campus! ☀️"
            ),
            Post(
                postId = "profile_sample3",
                userId = auth.currentUser?.uid ?: "user",
                userName = auth.currentUser?.displayName ?: "You",
                text = "Working on exciting projects! 💻"
            )
        )

        userPosts.addAll(samplePosts)
        postAdapter.notifyDataSetChanged()
        txtPostCount.text = "${userPosts.size} posts"
    }
}