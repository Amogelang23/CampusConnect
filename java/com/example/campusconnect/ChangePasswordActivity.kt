package com.example.campusconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var editOldPassword: EditText
    private lateinit var editNewPassword: EditText
    private lateinit var btnUpdatePassword: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()
        editOldPassword = findViewById(R.id.editOldPassword)
        editNewPassword = findViewById(R.id.editNewPassword)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)

        btnUpdatePassword.setOnClickListener {
            val oldPass = editOldPassword.text.toString().trim()
            val newPass = editNewPassword.text.toString().trim()

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
                    user.email ?: "", oldPass
                )

                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.updatePassword(newPass)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Failed to update password: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}
