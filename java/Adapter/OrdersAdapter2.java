package com.uilover.project2252.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uilover.project2252.R;
import com.uilover.project2252.objects.SellerOrders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter2 extends RecyclerView.Adapter<OrdersAdapter2.OrderViewHolder> {
    private static final String TAG = "OrdersAdapter";
    private List<SellerOrders> orders;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderAction(String orderId, String action);
    }

    public OrdersAdapter2(List<SellerOrders> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_seller, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        try {
            SellerOrders order = orders.get(position);
            holder.bind(order);
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdText;
        private TextView dateText;
        private TextView totalText;
        private TextView statusText;
        private Button acceptButton;
        private Button rejectButton;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                orderIdText = itemView.findViewById(R.id.orderIdText);
                dateText = itemView.findViewById(R.id.dateText);
                totalText = itemView.findViewById(R.id.totalText);
                statusText = itemView.findViewById(R.id.statusText);
                acceptButton = itemView.findViewById(R.id.acceptButton);
                rejectButton = itemView.findViewById(R.id.rejectButton);

                // Set click listeners in constructor
                acceptButton.setOnClickListener(v -> {
                    try {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            SellerOrders order = orders.get(position);
                            if (order != null && order.getOrderId() != null) {
                                Log.d(TAG, "Accept button clicked for order: " + order.getOrderId());
                                listener.onOrderAction(order.getOrderId(), "accepted");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in accept button click: " + e.getMessage(), e);
                    }
                });

                rejectButton.setOnClickListener(v -> {
                    try {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            SellerOrders order = orders.get(position);
                            if (order != null && order.getOrderId() != null) {
                                Log.d(TAG, "Reject button clicked for order: " + order.getOrderId());
                                listener.onOrderAction(order.getOrderId(), "rejected");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in reject button click: " + e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in ViewHolder constructor: " + e.getMessage(), e);
            }
        }

        void bind(SellerOrders order) {
            try {
                if (order == null) {
                    Log.e(TAG, "Attempting to bind null order");
                    return;
                }

                if (order.getOrderId() != null && order.getOrderId().length() >= 8) {
                    orderIdText.setText("Order #" + order.getOrderId().substring(0, 8));
                } else {
                    orderIdText.setText("Order #" + order.getOrderId());
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                dateText.setText(sdf.format(new Date(order.getTimestampMillis())));

                totalText.setText(String.format("Total: ₱%.2f", order.getTotalAmount()));
                statusText.setText("Status: " + order.getStatus());

                // Show/hide buttons based on order status
                if ("pending".equals(order.getStatus())) {
                    acceptButton.setVisibility(View.VISIBLE);
                    rejectButton.setVisibility(View.VISIBLE);
                } else {
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding order: " + e.getMessage(), e);
            }
        }
    }
}