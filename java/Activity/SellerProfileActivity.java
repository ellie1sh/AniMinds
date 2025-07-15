package com.uilover.project2252.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.uilover.project2252.R;

public class SellerProfileActivity extends AppCompatActivity {
    private static final String TAG = "SellerProfileActivity";

    private TextView profileCompany, profileEmail, profilePassword, profileContactNo;
    private TextView titleName, titleUser;
    private Button logoutButton, addProductsButton, viewOrdersButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_seller_profile);

            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Initialize views
            initializeViews();

            // Set up click listeners
            setupClickListeners();

            // Show user data
            showUserData();

            // Set up window insets
            setupWindowInsets();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            profileCompany = findViewById(R.id.profileCompany);
            profileEmail = findViewById(R.id.profileEmail);
            profilePassword = findViewById(R.id.profilePassword);
            profileContactNo = findViewById(R.id.profileContactNo);
            titleName = findViewById(R.id.titleName);
            titleUser = findViewById(R.id.titleUser);
            logoutButton = findViewById(R.id.logoutButton);
            addProductsButton = findViewById(R.id.addProductsButton);
            viewOrdersButton = findViewById(R.id.viewOrdersButton);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> {
            try {
                mAuth.signOut();
                startActivity(new Intent(SellerProfileActivity.this, LoginActivity.class));
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error during logout: " + e.getMessage(), e);
                Toast.makeText(this, "Error during logout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        addProductsButton.setOnClickListener(v -> {
            try {
                startActivity(new Intent(SellerProfileActivity.this, AddProductActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Error starting AddProductActivity: " + e.getMessage(), e);
                Toast.makeText(this, "Error opening Add Products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewOrdersButton.setOnClickListener(v -> {
            try {
                startActivity(new Intent(SellerProfileActivity.this, ViewOrdersActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Error starting ViewOrdersActivity: " + e.getMessage(), e);
                Toast.makeText(this, "Error opening View Orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWindowInsets() {
        try {
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    try {
                        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    } catch (Exception e) {
                        Log.e(TAG, "Error applying window insets: " + e.getMessage(), e);
                    }
                    return insets;
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up window insets: " + e.getMessage(), e);
        }
    }

    private void showUserData() {
        try {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(TAG, "Intent is null");
                return;
            }

            String companyUser = intent.getStringExtra("company");
            String emailUser = intent.getStringExtra("email");
            String passwordUser = intent.getStringExtra("password");
            String contactNoUser = intent.getStringExtra("number");

            if (companyUser != null) titleName.setText(companyUser);
            if (emailUser != null) titleUser.setText(emailUser);
            if (companyUser != null) profileCompany.setText(companyUser);
            if (emailUser != null) profileEmail.setText(emailUser);
            if (passwordUser != null) profilePassword.setText(passwordUser);
            if (contactNoUser != null) profileContactNo.setText(contactNoUser);
        } catch (Exception e) {
            Log.e(TAG, "Error showing user data: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}