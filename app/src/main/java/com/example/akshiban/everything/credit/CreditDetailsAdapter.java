package com.example.akshiban.everything.credit;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

class CreditDetailsAdapter extends RecyclerView.Adapter<CreditDetailsAdapter.ViewHolder> {

    List<CreditDetails> creditList;
    Context context;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    String email;

    public CreditDetailsAdapter(List<CreditDetails> creditList, Context context, ProgressBar progressBar) {
        this.creditList = creditList;
        this.context = context;
        this.progressBar = progressBar;
        firebaseFirestore = FirebaseFirestore.getInstance();
        email = GoogleSignIn.getLastSignedInAccount(context).getEmail();
    }

    @NonNull
    @Override
    public CreditDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CreditDetailsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.credit, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreditDetailsAdapter.ViewHolder viewHolder, int i) {
        CreditDetails creditDetails = creditList.get(i);
        viewHolder.credit_name.setText(creditDetails.getName());
        viewHolder.credit_balance.setText(creditDetails.getBalance() + "");
        viewHolder.view.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            EditText editText = new EditText(context);
            builder.setView(editText);
            builder.setTitle("Change balance");
            builder.setMessage("Enter your new balance?");
            builder.setPositiveButton("Change", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                int original = creditDetails.getBalance();
                int n = Integer.parseInt(editText.getText().toString());
                if (n != original) {
                    creditDetails.setBalance(n);
                    dialog.dismiss();
                    firebaseFirestore.collection("ids").document(email).collection("credit")
                            .document(creditDetails.getId()).set(creditDetails).addOnSuccessListener(c -> {
                        progressBar.setVisibility(View.GONE);
                        notifyItemChanged(i);
                        Toast.makeText(context, "Changed", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Values are same", Toast.LENGTH_SHORT).show();
                }
            }
            );
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView credit_name, credit_balance;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            credit_name = itemView.findViewById(R.id.credit_name);
            credit_balance = itemView.findViewById(R.id.credit_balance);
        }
    }
}
