package com.example.akshiban.everything.notes;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akshiban.everything.R;

import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DataFileAdapter extends RecyclerView.Adapter<DataFileAdapter.ViewHolder> {

    List<DataFile> files;
    Context context;
    DownloadManager downloadManager;


    public DataFileAdapter(List<DataFile> files, Context context) {
        this.files = files;
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @NonNull
    @Override
    public DataFileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DataFileAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_file_view,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DataFileAdapter.ViewHolder viewHolder, int i) {
        DataFile file = files.get(i);
        viewHolder.file_name.setText(file.getFileName());
        viewHolder.download.setOnClickListener(v->{
            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse(file.getDownloadURL());
            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, file.getFileName());
            downloadManager.enqueue(request);
        });
        viewHolder.view.setOnClickListener(v->{
            Intent intent = new Intent(context, PdfView.class);
            intent.putExtra("pdfUrl", file.getDownloadURL());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        ImageButton download, view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.data_file_name);
            download = itemView.findViewById(R.id.notes_download_file);
            view = itemView.findViewById(R.id.notes_view_file);
        }
    }
}
