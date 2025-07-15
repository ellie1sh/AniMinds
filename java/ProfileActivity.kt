package com.uilover.project2252.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uilover.project2252.R
import com.uilover.project2252.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setVariable()
        showUserData()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Logout button logic
        val logoutButton = findViewById<View?>(R.id.logoutButton)
        logoutButton.setOnClickListener(View.OnClickListener { v: View? ->
            // Clear session data (if any)
            getSharedPreferences("userSession", MODE_PRIVATE).edit().clear().apply()

            // Redirect to login screen
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        })
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun showUserData() {
        val intent = intent

        val companyUser = intent.getStringExtra("company") ?: "N/A"
        val emailUser = intent.getStringExtra("email") ?: "N/A"
        val passwordUser = intent.getStringExtra("password") ?: "N/A"
        val contactNoUser = intent.getStringExtra("number") ?: "N/A"

        binding.titleName.text = companyUser
        binding.titleUser.text = emailUser
        binding.profileEmail.text = emailUser
        binding.profilePassword.text = passwordUser
        binding.profileContactNo.text = contactNoUser
    }
}