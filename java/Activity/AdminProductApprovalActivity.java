package com.uilover.project2252.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uilover.project2252.Adapter.ProductApprovalsAdapter;
import com.uilover.project2252.R;
import com.uilover.project2252.objects.ProductApprovalRequest;

import java.util.ArrayList;
import java.util.List;

public class AdminProductApprovalActivity extends AppCompatActivity
        implements ProductApprovalsAdapter.OnApprovalActionListener {

    private RecyclerView recyclerView;
    private ProductApprovalsAdapter adapter;
    private List<ProductApprovalRequest> requests = new ArrayList<>();
    private DatabaseReference requestsRef;
    private BottomNavigationView bottomNavigationView;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_productapproval);

        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        emptyStateText = findViewById(R.id.emptyStateText2);

        adapter = new ProductApprovalsAdapter(requests, this);
        recyclerView.setAdapter(adapter);

        requestsRef = FirebaseDatabase.getInstance().getReference("productApprovalRequests");
        loadRequests();
    }

    private void loadRequests() {
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProductApprovalRequest request = snapshot.getValue(ProductApprovalRequest.class);
                    if (request != null) {
                        request.setRequestId(snapshot.getKey());
                        requests.add(request);
                    }
                }
                adapter.notifyDataSetChanged();

                if (requests.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminProductApprovalActivity.this,
                        "Failed to load requests: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(int position) {
        ProductApprovalRequest request = requests.get(position);
        approveProduct(request);
        requests.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onReject(int position) {
        ProductApprovalRequest request = requests.get(position);
        rejectProduct(request);
        requests.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void approveProduct(ProductApprovalRequest request) {
        DatabaseReference approvedRef = FirebaseDatabase.getInstance()
                .getReference("approvedProducts")
                .child(request.getRequestId());

        approvedRef.setValue(request)
                .addOnSuccessListener(aVoid -> {
                    requestsRef.child(request.getRequestId()).removeValue();
                    Toast.makeText(this, "Product approved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Approval failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectProduct(ProductApprovalRequest request) {
        requestsRef.child(request.getRequestId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Rejection failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.product) {
            return true;
        } else if (itemId == R.id.application) {
            startActivity(new Intent(this, AdminSellerApplicationsActivity.class));
            return true;
        }
        return false;
    }
}