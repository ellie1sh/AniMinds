package com.uilover.project2252.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uilover.project2252.Adapter.OrdersAdapter2;
import com.uilover.project2252.R;
import com.uilover.project2252.objects.SellerOrders;
import java.util.ArrayList;
import java.util.List;

public class ViewOrdersActivity extends AppCompatActivity implements OrdersAdapter2.OnOrderActionListener {
    private static final String TAG = "ViewOrdersActivity";
    private RecyclerView recyclerView;
    private OrdersAdapter2 adapter;
    private List<SellerOrders> orders = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView emptyStateText;

    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            recyclerView = findViewById(R.id.ordersRecyclerView);
            emptyStateText = findViewById(R.id.emptyStateText);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new OrdersAdapter2(orders, this);
            recyclerView.setAdapter(adapter);

            backButton = findViewById(R.id.backButton);

            backButton.setOnClickListener(v -> goBackToSellerProfile());
            loadOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadOrders() {
        try {
            String sellerId = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "Loading orders for seller: " + sellerId);

            db.collection("Orders")
                    .whereEqualTo("status", "pending")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e(TAG, "Error loading orders: " + error.getMessage());
                            Toast.makeText(ViewOrdersActivity.this,
                                    "Error loading orders: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            orders.clear();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                                    SellerOrders order = snapshot.toObject(SellerOrders.class);
                                    if (order != null) {
                                        order.setOrderId(snapshot.getId());
                                        orders.add(order);
                                        Log.d(TAG, "Loaded order: " + order.getOrderId());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        } catch (Exception e) {
                            Log.e(TAG, "Error in onDataChange: " + e.getMessage(), e);
                            Toast.makeText(ViewOrdersActivity.this,
                                    "Error loading orders: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadOrders: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateEmptyState() {
        if (orders.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOrderAction(String orderId, String action) {
        try {
            if (orderId == null || orderId.isEmpty()) {
                Log.e(TAG, "Invalid order ID");
                Toast.makeText(this, "Error: Invalid order ID", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("Orders").document(orderId)
                    .update("status", action)
                    .addOnSuccessListener(aVoid -> {
                        String message = action.equals("accepted") ?
                                "Order accepted" : "Order rejected";
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Order " + orderId + " " + action);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update order: " + e.getMessage(), e);
                        Toast.makeText(this, "Failed to update order: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in onOrderAction: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating order: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
}