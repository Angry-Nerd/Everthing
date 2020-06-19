package com.example.akshiban.everything.locker;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Locker extends AppCompatActivity {


    private static final int SAVE_REQUEST_CODE = 2828;
    private FloatingActionButton button;
    private RecyclerView recyclerView;
    private List<LockerItem> itemList;
    private LockerAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private GoogleSignInAccount acct;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);


        button = findViewById(R.id.addToLockerBtn);
        recyclerView = findViewById(R.id.lockerView);
        progressBar = findViewById(R.id.lockerProgressBar);

        itemList = new ArrayList<>();
        acct = GoogleSignIn.getLastSignedInAccount(this);
        adapter = new LockerAdapter(itemList, this, acct.getEmail(), progressBar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        fetch();
    }

    private void fetch() {
        db = FirebaseFirestore.getInstance();
        db.collection("ids").document(acct.getEmail()).collection("locker").get().addOnSuccessListener(docs -> {
            for (DocumentSnapshot ds : docs.getDocuments()) {
                LockerItem item = ds.toObject(LockerItem.class);
                item.setId(ds.getId());
                itemList.add(item);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                itemList.sort((a, b) -> (int) (a.getDateCreated() - b.getDateCreated()));
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });


        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, SAVE_REQUEST_CODE);
        });


    }


    private String getFileName(Uri content) {
        String result = null;
        if (content.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(content, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = content.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri content = data.getData();
            if (checkFileSize(content)) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);

            builder.setTitle("Name of the file");
            builder.setTitle("What's the name?");
            builder.setView(editText);

            builder.setPositiveButton("Add", (dialog, which) -> {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(Locker.this, "Please add a name", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    storage = FirebaseStorage.getInstance();
                    String fileName = getFileName(content);
                    String[] file = fileName.split("\\.");
                    long now = new Date().getTime();
                    StorageReference reference = storage.getReference("ids/" + acct.getEmail()).child("locker/" + editText.getText().toString());
                    reference.putFile(content).addOnSuccessListener(s -> {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            String id = db.collection("ids").document(acct.getEmail()).collection("locker").document().getId();
                            LockerItem item = new LockerItem(editText.getText().toString(), url, now);
                            if (file.length >= 2)
                                item.setTypeOfFile(file[file.length - 1]);
                            else item.setTypeOfFile("file");
                            item.setId(id);
                            db.collection("ids").document(acct.getEmail()).collection("locker").document(id).set(item).addOnSuccessListener(v -> {
                                itemList.add(item);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                dialog.dismiss();
                            });
                        });
                    }).addOnFailureListener(f -> {
                        dialog.dismiss();
                        Toast.makeText(this, "File can't be added", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            builder.show();

        }
    }

    private boolean checkFileSize(Uri uri) {
        File file = new File(uri.getPath());
//        Cursor returnCursor =
//                getContentResolver().query(uri, null, null, null, null);
//
        Toast.makeText(this, "" + file.length(), Toast.LENGTH_SHORT).show();
        return file.length() >= 1024 * 1024 * 1024;

    }
}
