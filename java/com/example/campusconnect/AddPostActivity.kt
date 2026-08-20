package com.example.campusconnect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private lateinit var captionField: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelectImage: Button
    private var imageUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Posts")
    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        imgPreview = findViewById(R.id.imgPreview)
        captionField = findViewById(R.id.captionField)
        btnUpload = findViewById(R.id.btnUpload)
        btnSelectImage = findViewById(R.id.btnSelectImage)

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        btnUpload.setOnClickListener {
            uploadPost()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imgPreview.setImageURI(imageUri)
        }
    }

    private fun uploadPost() {
        val caption = captionField.text.toString().trim()
        val user = auth.currentUser ?: return

        if (caption.isEmpty()) {
            Toast.makeText(this, "Please write a caption", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            // Text-only post
            savePostToDatabase(caption, null)
            return
        }

        // Upload image first
        val filename = "posts/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(filename)

        btnUpload.isEnabled = false
        btnUpload.text = "Uploading..."

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    savePostToDatabase(caption, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                btnUpload.isEnabled = true
                btnUpload.text = "Upload Post"
            }
    }

    private fun savePostToDatabase(caption: String, imageUrl: String?) {
        val user = auth.currentUser ?: return
        val postId = dbRef.push().key ?: return

        val post = Post(
            postId = postId,
            userId = user.uid,
            userName = user.displayName ?: "Anonymous User",
            userProfile = user.photoUrl?.toString(),
            text = caption,
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis(),
            likes = 0
        )

        dbRef.child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save post: ${it.message}", Toast.LENGTH_SHORT).show()
                btnUpload.isEnabled = true
                btnUpload.text = "Upload Post"
            }
    }
}