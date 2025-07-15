package com.uilover.project2252.objects;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class SellerOrders {
    private String orderId;
    private String sellerId;
    private String userId;
    private String status;
    private double totalAmount;
    private Timestamp timestamp;  // Changed from long to Timestamp
    private List<Map<String, Object>> items;

    public SellerOrders() {}

    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    // Helper method to get timestamp as long
    public long getTimestampMillis() {
        return timestamp != null ? timestamp.toDate().getTime() : 0L;
    }
}