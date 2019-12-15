package com.example.akshiban.everything.hisaab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.akshiban.everything.R;

import java.util.List;

class WalletTypeAdapter extends RecyclerView.Adapter<WalletTypeAdapter.ViewHolder> {

    List<WalletMetaData> walletList;
    Context context;

    public WalletTypeAdapter(List<WalletMetaData> walletList, Context context) {
        this.walletList = walletList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallet_type, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.wallet_name.setText(walletList.get(i).getWalletName());
        String total = "Current balance: " + walletList.get(i).getCurrentBalance();
        viewHolder.wallet_current_balance.setText(total);
        viewHolder.wallet_initial_balance.setText("Initial balance: " + walletList.get(i).getInitialBalance());
        viewHolder.view.setOnClickListener(v -> {
            Intent intent = new Intent(context, WalletDetails.class);
            intent.putExtra("walletId", walletList.get(i));
            context.startActivity(intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wallet_name, wallet_current_balance, wallet_initial_balance;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            wallet_name = itemView.findViewById(R.id.wallet_name);
            wallet_current_balance = itemView.findViewById(R.id.wallet_current_balance);
            wallet_initial_balance = itemView.findViewById(R.id.wallet_initial_balance);
        }
    }
}
