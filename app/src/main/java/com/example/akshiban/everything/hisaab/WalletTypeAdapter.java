package com.example.akshiban.everything.hisaab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.akshiban.everything.R;

import java.util.List;

class WalletTypeAdapter extends ListAdapter<WalletMetaData, WalletTypeAdapter.ViewHolder> {

//    List<WalletMetaData> walletList;
//    Context context;
    private OnItemClickListener listener;

    public WalletTypeAdapter() {
        super(DIIF_CALLBACK);
    }

    private static DiffUtil.ItemCallback<WalletMetaData> DIIF_CALLBACK = new DiffUtil.ItemCallback<WalletMetaData>() {
        @Override
        public boolean areItemsTheSame(@NonNull WalletMetaData metaData, @NonNull WalletMetaData t1) {
            return metaData.getId().equals(t1.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WalletMetaData m1, @NonNull WalletMetaData m2) {
            return m1.getId().equals(m2.getId()) && m1.getWalletName().equals(m2.getWalletName());
        }
    };

    public WalletMetaData getItemAt(int position) {
        return getItem(position);
    }


//    public WalletTypeAdapter(List<WalletMetaData> walletList, Context context) {
//        this.walletList = walletList;
//        this.context = context;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallet_type, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.wallet_name.setText(getItem(i).getWalletName());
        String total = "Current balance: " + getItem(i).getCurrentBalance();
        viewHolder.wallet_current_balance.setText(total);
        viewHolder.wallet_initial_balance.setText("Initial balance: " + getItem(i).getInitialBalance());

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView wallet_name, wallet_current_balance, wallet_initial_balance;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            wallet_name = itemView.findViewById(R.id.wallet_name);
            wallet_current_balance = itemView.findViewById(R.id.wallet_current_balance);
            wallet_initial_balance = itemView.findViewById(R.id.wallet_initial_balance);
            view.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(getItem(getAdapterPosition()));
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(WalletMetaData note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
