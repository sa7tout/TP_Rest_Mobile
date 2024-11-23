package com.example.banqueapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.banqueapp.R;
import com.example.banqueapp.model.Compte;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompteAdapter extends ListAdapter<Compte, CompteAdapter.CompteViewHolder> {
    private final OnCompteActionListener listener;

    public interface OnCompteActionListener {
        void onEdit(Compte compte);
        void onDelete(Compte compte);
    }

    @NonNull
    @Override
    public CompteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compte, parent, false);
        return new CompteViewHolder(view);
    }

    public CompteAdapter(OnCompteActionListener listener) {
        super(new DiffUtil.ItemCallback<Compte>() {
            @Override
            public boolean areItemsTheSame(@NonNull Compte oldItem, @NonNull Compte newItem) {
                return oldItem != null && newItem != null &&
                        oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Compte oldItem, @NonNull Compte newItem) {
                if (oldItem == null || newItem == null) return false;

                return oldItem.getSolde() == newItem.getSolde() &&
                        Objects.equals(oldItem.getType(), newItem.getType()) &&
                        Objects.equals(oldItem.getDateCreation(), newItem.getDateCreation());
            }
        });
        this.listener = listener;
    }

    static class CompteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId;
        private final TextView tvSolde;
        private final TextView tvType;
        private final TextView tvDate;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public CompteViewHolder(@NonNull View view) {
            super(view);
            tvId = view.findViewById(R.id.tvId);
            tvSolde = view.findViewById(R.id.tvSolde);
            tvType = view.findViewById(R.id.tvType);
            tvDate = view.findViewById(R.id.tvDate);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }

        void bind(Compte compte, OnCompteActionListener listener) {
            if (compte != null) {
                tvId.setText(String.format("ID: %s", compte.getId()));
                tvSolde.setText(String.format("Balance: %.2f", compte.getSolde()));
                tvType.setText(String.format("Type: %s",
                        compte.getType() != null ? compte.getType().toString() : "N/A"));
                tvDate.setText(String.format("Created: %s",
                        compte.getDateCreation() != null ? compte.getDateCreation() : "N/A"));

                btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEdit(compte);
                    }
                });
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDelete(compte);
                    }
                });

                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CompteViewHolder holder, int position) {
        Compte compte = getItem(position);
        holder.bind(compte, listener);
    }

    @Override
    public void submitList(List<Compte> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }
}