package com.uilover.project2252.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uilover.project2252.R;
import com.uilover.project2252.objects.ProductApprovalRequest;

import java.util.List;



public class ProductApprovalsAdapter extends RecyclerView.Adapter<ProductApprovalsAdapter.ViewHolder> {

    private List<ProductApprovalRequest> requests;
    private OnApprovalActionListener listener;

    public interface OnApprovalActionListener {
        void onApprove(int position);
        void onReject(int position);
    }

    public ProductApprovalsAdapter(List<ProductApprovalRequest> requests, OnApprovalActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductApprovalRequest request = requests.get(position);

        holder.productNameText.setText(request.getProductName());
        holder.priceText.setText(String.format("Price: ₱%.2f", request.getPrice()));

        holder.approveButton.setOnClickListener(v -> listener.onApprove(position));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText, priceText;
        Button approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.productNameText);
            priceText = itemView.findViewById(R.id.priceText);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}