package com.uilover.project2252.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project2252.databinding.ItemOrderBinding
import com.uilover.project2252.objects.OrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter : ListAdapter<OrderModel, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {
    private val mStore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderModel) {
            binding.apply {
                // Set order ID
                orderIdText.text = "Order #${order.orderId.take(8)}"

                // Set order date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                orderDateText.text = dateFormat.format(Date(order.orderDate))

                // Set order status
                orderStatusText.text = order.status.capitalize()
                orderStatusText.setBackgroundResource(
                    when (order.status.lowercase()) {
                        "pending" -> com.uilover.project2252.R.drawable.status_background
                        "confirmed" -> com.uilover.project2252.R.drawable.status_background
                        "completed" -> com.uilover.project2252.R.drawable.status_background
                        "cancelled" -> com.uilover.project2252.R.drawable.status_background
                        else -> com.uilover.project2252.R.drawable.status_background
                    }
                )

                // Set total amount
                totalAmountText.text = "₱${String.format("%.2f", order.totalAmount)}"

                // Set first item image if available
                if (order.items.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(order.items[0].imageUrl)
                        .into(firstItemImage)
                }

                // Show/hide cancel button based on order status
                cancelButton.visibility = if (order.status.lowercase() == "pending") {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Set up cancel button click listener
                cancelButton.setOnClickListener {
                    cancelOrder(order.orderId)
                }
            }
        }

        private fun cancelOrder(orderId: String) {
            mStore.collection("Orders")
                .document(orderId)
                .update("status", "cancelled")
                .addOnSuccessListener {
                    Toast.makeText(
                        itemView.context,
                        "Order cancelled successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        itemView.context,
                        "Failed to cancel order: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }
    }
} 