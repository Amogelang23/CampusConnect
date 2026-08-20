package com.example.campusconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Load default fragment
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> loadFragment(HomeFragment())
                R.id.navChat -> loadFragment(ChatFragment())
                R.id.navCreatePost -> loadFragment(CreatePostFragment())
                R.id.navFriends -> loadFragment(FriendsFragment())
                R.id.navProfile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    // CHANGE: Remove 'private' to make it accessible from fragments
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}