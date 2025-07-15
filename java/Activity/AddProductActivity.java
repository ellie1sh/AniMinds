package com.uilover.project2252.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uilover.project2252.R;
import com.uilover.project2252.objects.Product;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    private TextInputEditText titleInput, descriptionInput, priceInput, categoryInput;
    private Button submitButton;
    private DatabaseReference productsRef;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView productImageView;
    private Button uploadImageButton;
    private ImageButton backButton;
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/120";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        try {
            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            productsRef = FirebaseDatabase.getInstance().getReference("products");

            // Initialize views
            titleInput = findViewById(R.id.titleInput);
            descriptionInput = findViewById(R.id.descriptionInput);
            priceInput = findViewById(R.id.priceInput);
            categoryInput = findViewById(R.id.categoryInput);
            submitButton = findViewById(R.id.submitButton);
            productImageView = findViewById(R.id.productImageView);
            uploadImageButton = findViewById(R.id.uploadImageButton);
            backButton = findViewById(R.id.backButton);

            backButton.setOnClickListener(v -> goBackToSellerProfile());
            uploadImageButton.setOnClickListener(v -> openFileChooser());
            submitButton.setOnClickListener(v -> addProduct());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), PICK_IMAGE_REQUEST);
    }

    private void goBackToSellerProfile() {
        try {
            Intent intent = new Intent(this, SellerProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error going back: " + e.getMessage(), e);
            Toast.makeText(this, "Error: Couldn't go back", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                productImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            }
        }
    }

    private void addProduct() {
        try {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String category = categoryInput.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    Toast.makeText(this, "Price must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            String sellerId = mAuth.getCurrentUser().getUid();
            if (sellerId == null || sellerId.isEmpty()) {
                Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Always use the default image URL as an ArrayList
            ArrayList<String> picUrls = new ArrayList<>();
            picUrls.add(DEFAULT_IMAGE_URL);
            Product product = new Product(title, description, price, category, sellerId);
            product.setPicUrl(picUrls);
            String productId = productsRef.push().getKey();
            if (productId != null) {
                productsRef.child(productId).setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddProductActivity.this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in addProduct: " + e.getMessage(), e);
            Toast.makeText(this, "Error adding product: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
} 