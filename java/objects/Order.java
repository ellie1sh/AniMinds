package com.uilover.project2252.objects;

import java.util.Map;

public class Order {
    private String id;
    private String sellerId;
    private String customerName;
    private double total;
    private String status;
    private Map<String, Product> items;
    private long timestamp;

    public Order() {
    }

    public Order(String id, String customerName, double total, String status) {
        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Product> getItems() { return items; }
    public void setItems(Map<String, Product> items) { this.items = items; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSellerId() {
        return sellerId;
    }


    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}

