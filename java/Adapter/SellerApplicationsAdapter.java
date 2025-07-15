package com.uilover.project2252.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uilover.project2252.R;
import com.uilover.project2252.objects.SellerApplication;

import java.util.List;



public class SellerApplicationsAdapter extends RecyclerView.Adapter<SellerApplicationsAdapter.ViewHolder> {

    private List<SellerApplication> applications;
    private OnApplicationActionListener listener;

    public interface OnApplicationActionListener {
        void onAccept(int position);
        void onReject(int position);
    }

    public SellerApplicationsAdapter(List<SellerApplication> applications, OnApplicationActionListener listener) {
        this.applications = applications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerApplication application = applications.get(position);

        holder.usernameText.setText("Username: " + application.getUsername());
        holder.emailText.setText("Email: " + application.getEmail());

        holder.acceptButton.setOnClickListener(v -> listener.onAccept(position));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(position));
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, emailText;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            emailText = itemView.findViewById(R.id.emailText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}