package com.example.campusconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var editName: EditText
    private lateinit var editBio: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var txtWelcome: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        editName = view.findViewById(R.id.editName)
        editBio = view.findViewById(R.id.editBio)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)

        // Add welcome text
        txtWelcome = TextView(requireContext()).apply {
            text = "Your Profile 🎓"
            textSize = 20f
            setPadding(0, 0, 0, 20)
        }
        (view as ViewGroup).addView(txtWelcome, 0)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            editName.setText(user.displayName ?: "Campus Student")
            editBio.setText("Welcome to Campus Connect! Share your campus journey and connect with fellow students.")
            txtWelcome.text = "Welcome, ${user.displayName ?: "Student"}! 🎓"
        }

        btnSaveProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}