package com.uilover.project2252.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project195.Helper.ChangeNumberItemsListener
import com.uilover.project2252.Adapter.CartAdapter
import com.uilover.project2252.databinding.ActivityCartBinding
import com.uilover.project2252.objects.OrderItem
import com.uilover.project2252.objects.OrderModel
import java.util.UUID

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        mAuth = FirebaseAuth.getInstance()
        mStore = FirebaseFirestore.getInstance()

        calculateCart()
        setVariable()
        initCartList()
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            listView.adapter = CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
        
        // Add checkout button click listener
        binding.button3.setOnClickListener {
            proceedToCheckout()
        }
    }

    private fun proceedToCheckout() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItems = managmentCart.getListCart()
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert cart items to order items
        val orderItems = cartItems.map { item ->
            OrderItem(
                itemId = item.id,
                title = item.title,
                price = item.price,
                quantity = item.numberInCart,
                imageUrl = item.picUrl[0]
            )
        }

        // Create new order
        val order = OrderModel(
            orderId = UUID.randomUUID().toString(),
            userId = currentUser.uid,
            items = orderItems,
            totalAmount = managmentCart.getTotalFee() + tax + 15, // Including tax and delivery
            status = "pending",
            orderDate = System.currentTimeMillis()
        )

        // Save order to Firestore
        mStore.collection("Orders")
            .document(order.orderId)
            .set(order)
            .addOnSuccessListener {
                // Clear cart after successful order
                managmentCart.clearCart()
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                
                // Navigate to OrdersActivity
                startActivity(Intent(this, OrdersActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 15
        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100
        binding.apply {
            totalFeeTxt.text = "₱$itemTotal"
            taxTxt.text = "₱$tax"
            deliveryTxt.text = "₱$delivery"
            totalTxt.text = "₱$total"
        }
    }
}