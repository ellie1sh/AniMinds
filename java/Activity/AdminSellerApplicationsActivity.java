package com.uilover.project2252.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uilover.project2252.Adapter.SellerApplicationsAdapter;
import com.uilover.project2252.R;
import com.uilover.project2252.objects.SellerApplication;

import java.util.ArrayList;
import java.util.List;



public class AdminSellerApplicationsActivity extends AppCompatActivity
        implements SellerApplicationsAdapter.OnApplicationActionListener {

    private RecyclerView recyclerView;
    private SellerApplicationsAdapter adapter;
    private List<SellerApplication> applications = new ArrayList<>();
    private DatabaseReference applicationsRef;
    private FirebaseFirestore firestore;
    private BottomNavigationView bottomNavigationView;
    private TextView emptyStateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sellerapplications);

        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase instances
        applicationsRef = FirebaseDatabase.getInstance().getReference("sellerApplications");
        firestore = FirebaseFirestore.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        emptyStateText = findViewById(R.id.emptyStateText);

        adapter = new SellerApplicationsAdapter(applications, this);
        recyclerView.setAdapter(adapter);


        loadApplications();
    }

    private void loadApplications() {
        applicationsRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applications.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SellerApplication application = snapshot.getValue(SellerApplication.class);
                            if (application != null) {
                                application.setId(snapshot.getKey());
                                applications.add(application);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (applications.isEmpty()) {
                            emptyStateText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyStateText.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AdminSellerApplicationsActivity.this,
                                "Failed to load applications: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAccept(int position) {
        if (position < 0 || position >= applications.size()) return;

        SellerApplication application = applications.get(position);
        application.setStatus("approved");

        applicationsRef.child(application.getId())
                .setValue(application)
                .addOnSuccessListener(aVoid -> {
                    updateFirestoreSellerStatus(application.getId(), true);

                    int currentIndex = applications.indexOf(application);
                    if (currentIndex != -1) {
                        applications.remove(currentIndex);
                        adapter.notifyItemRemoved(currentIndex);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    Toast.makeText(this, "Application approved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to approve: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFirestoreSellerStatus(String userID, boolean isSeller) {
        firestore.collection("Users")
                .document(userID)
                .update("isSeller", isSeller ? "1" : "0")
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Seller status updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating seller status", e);
                    Toast.makeText(this, "Failed to update seller status", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onReject(int position) {
        if (position < 0 || position >= applications.size()) return;

        SellerApplication application = applications.get(position);
        application.setStatus("rejected");

        applicationsRef.child(application.getId())
                .setValue(application)
                .addOnSuccessListener(aVoid -> {
                    applications.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Application rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to reject: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.product) {

            startActivity(new Intent(this, AdminProductApprovalActivity.class));
            return true;
        } else if (itemId == R.id.application) {
            return true;
        }
        return false;
    }
}