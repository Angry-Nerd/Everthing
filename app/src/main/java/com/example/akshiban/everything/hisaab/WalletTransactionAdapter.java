package com.example.akshiban.everything.hisaab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.akshiban.everything.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder> {

    List<WalletTransaction> transactions;
    Context context;

    public WalletTransactionAdapter(List<WalletTransaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WalletTransaction transaction = transactions.get(i);
        viewHolder.details.setText(transactions.get(i).getDescription());
        viewHolder.closing_bal.setText("Closing Balance: " + transaction.getClosingBal());
        viewHolder.opening_bal.setText("Opening Balance: " + transaction.getOpeningBal());
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date(transaction.getDate()));
        viewHolder.date.setText(date);
        viewHolder.transactionCost.setText(transaction.getTransactionCost() + "");
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void removeItem(int position) {
        transactions.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(WalletTransaction item, int position) {
        transactions.add(position, item);
        notifyItemInserted(position);
    }

    public List<WalletTransaction> getData() {
        return transactions;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView closing_bal, opening_bal, details, date, transactionCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionCost = itemView.findViewById(R.id.transaction_cost);
            closing_bal = itemView.findViewById(R.id.closing_bal);
            opening_bal = itemView.findViewById(R.id.opening_bal);
            details = itemView.findViewById(R.id.transaction_description);
            date = itemView.findViewById(R.id.transaction_date);
        }
    }
}
