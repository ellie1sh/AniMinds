package com.uilover.project2252.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project2252.Adapter.OrdersAdapter
import com.uilover.project2252.databinding.ActivityOrdersBinding
import com.uilover.project2252.objects.OrderModel

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStore: FirebaseFirestore
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mStore = FirebaseFirestore.getInstance()

        setupUI()
        loadOrders()
    }

    private fun setupUI() {
        binding.backBtn.setOnClickListener { finish() }
        
        // Create and setup OrdersAdapter
        ordersAdapter = OrdersAdapter()
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrdersActivity)
            adapter = ordersAdapter
        }
    }

    private fun loadOrders() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view orders", Toast.LENGTH_SHORT).show()
            return
        }

        mStore.collection("Orders")
            .whereEqualTo("userId", currentUser.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading orders: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(OrderModel::class.java)
                } ?: listOf()

                if (orders.isEmpty()) {
                    binding.emptyOrdersText.visibility = android.view.View.VISIBLE
                    binding.ordersRecyclerView.visibility = android.view.View.GONE
                } else {
                    binding.emptyOrdersText.visibility = android.view.View.GONE
                    binding.ordersRecyclerView.visibility = android.view.View.VISIBLE
                    ordersAdapter.submitList(orders)
                }
            }
    }
} 