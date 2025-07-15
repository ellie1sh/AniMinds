package com.uilover.project2252.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project2252.Helper.TinyDB
import com.uilover.project2252.databinding.ActivityProfileBinding

class BuyerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var tinyDB: TinyDB
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)
        mAuth = FirebaseAuth.getInstance()
        mStore = FirebaseFirestore.getInstance()

        // Set up back button
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Set up logout button
        binding.logoutButton2.setOnClickListener {
            // Clear user data on logout
            tinyDB.remove("user_email")
            tinyDB.remove("user_contact")
            tinyDB.remove("user_name")
            tinyDB.remove("user_username")
            
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Get current user
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // Fetch user data from Firestore
            mStore.collection("Users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("Username") ?: "No username"
                        val email = document.getString("Email") ?: "No email"
                        val phoneNumber = document.getString("PhoneNumber") ?: "No contact number"

                        // Update UI with user data
                        binding.titleName.text = username
                        binding.titleUser.text = username
                        binding.profileEmail.text = email
                        binding.profileContactNo.text = phoneNumber
                        binding.profilePassword.text = "********" // Masked password for security
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    binding.titleName.text = "Error loading profile"
                    binding.titleUser.text = "Please try again"
                }
        }
    }
} 