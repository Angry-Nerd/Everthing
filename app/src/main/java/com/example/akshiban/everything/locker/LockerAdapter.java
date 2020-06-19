package com.example.akshiban.everything.locker;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.akshiban.everything.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

class LockerAdapter extends RecyclerView.Adapter<LockerAdapter.ViewHolder> {

    private List<LockerItem> items;
    private Context context;
    private DownloadManager downloadManager;
    private StorageReference ref;
    private FirebaseFirestore db;
    private String email;
    private ProgressBar progressBar;

    public LockerAdapter(List<LockerItem> items, Context context, String email, ProgressBar progressBar) {
        this.items = items;
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance().getReference();
        this.email = email;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public LockerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LockerAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locker_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LockerAdapter.ViewHolder viewHolder, int i) {
        LockerItem item = items.get(i);
        viewHolder.nameOfItem.setText(item.getNameOfItem());
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date(item.getDateCreated()));
        viewHolder.dateCreated.setText(date);

        switch (item.getTypeOfFile()) {
            case "mp4":
            case "mkv":
                viewHolder.circleImageView.setImageDrawable(context.getDrawable(R.drawable.ic_video_black_24dp));
                break;
            case "jpg":
            case "png":
            case "jpeg":
                Glide.with(context).load(item.getUrlOfImage()).into(viewHolder.circleImageView);
                break;
            case "pdf":
                viewHolder.circleImageView.setImageDrawable(context.getDrawable(R.drawable.ic_picture_as_pdf_black_24dp));
                break;
            default:
                viewHolder.circleImageView.setImageDrawable(context.getDrawable(R.drawable.ic_insert_drive_file_black_24dp));
                break;
        }



        viewHolder.deleteItem.setOnClickListener(v -> deleteItem(item, i));

        viewHolder.downloadItem.setOnClickListener(v -> downloadFile(item.getUrlOfImage(), item.getNameOfItem(), item.getTypeOfFile()));
    }

    private void deleteItem(LockerItem item, int i) {

        progressBar.setVisibility(View.VISIBLE);
        ref.child("ids").child(email).child("locker").child(item.getNameOfItem()).delete().addOnSuccessListener(s -> {
            db.collection("ids").document(email).collection("locker").document(item.getId()).delete().addOnSuccessListener(v -> {
                items.remove(i);
                notifyItemRemoved(i);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(f->{
                Toast.makeText(context, "Can't delete now", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
        }).addOnFailureListener(f->{
            Toast.makeText(context, "Can't delete now", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });


    }

    private void downloadFile(String url, String name, String type) {
        Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, "/Everything/", name+"."+type);
        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateCreated, nameOfItem;
        ImageButton downloadItem, deleteItem;
        CircleImageView circleImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.lockerItemImage);
            dateCreated = itemView.findViewById(R.id.lockerItemDateAdded);
            nameOfItem = itemView.findViewById(R.id.nameOfLockerItem);
            downloadItem = itemView.findViewById(R.id.downloadLockerItem);
            deleteItem = itemView.findViewById(R.id.deleteLockerItem);
        }
    }
}
