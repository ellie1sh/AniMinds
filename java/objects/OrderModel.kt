package com.uilover.project2252.objects

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = listOf(),
    val totalAmount: Double = 0.0,
    val status: String = "pending",
    val orderDate: Long = System.currentTimeMillis()
)

data class OrderItem(
    val itemId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = ""
) 