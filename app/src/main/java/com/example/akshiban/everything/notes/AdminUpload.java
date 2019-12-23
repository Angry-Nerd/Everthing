package com.example.akshiban.everything.notes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class AdminUpload extends AppCompatActivity {


    private Spinner semesterSpinner, subjectSpinner;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private ArrayList<KV> subjectsList, semestersList;
    private SpinnerAdapter semesterAdapter, subjectsAdapter;
    private ProgressBar progressBar;
    private KV defKv;
    private Button select, upload;
    private int SAVE_REQUEST_CODE = 2828;
    private Uri content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload);


        firebaseFirestore = FirebaseFirestore.getInstance();
        semesterSpinner = findViewById(R.id.upload_semester_spinner);
        subjectSpinner = findViewById(R.id.upload_subject_spinner);
        subjectsList = new ArrayList<>();
        semestersList = new ArrayList<>();

        defKv = new KV();
        defKv.setKey("Select semester");
        defKv.setDateModified(0);
        defKv.setDateCreated(0);
        defKv.setValue("0");

        semestersList.add(defKv);
        defKv.setKey("Select subject");
        subjectsList.add(defKv);
        upload = findViewById(R.id.upload_notes);
        select = findViewById(R.id.select_file);

        semesterAdapter = new SpinnerAdapter(this, semestersList);
        subjectsAdapter = new SpinnerAdapter(this, subjectsList);

        progressBar = findViewById(R.id.upload_semester_progress);

        semesterSpinner.setAdapter(semesterAdapter);
        subjectSpinner.setAdapter(subjectsAdapter);
        subjectSpinner.setClickable(false);


        select.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, SAVE_REQUEST_CODE);
        });

        upload.setOnClickListener(v -> {
            if (content == null)
                Toast.makeText(this, "First upload a file", Toast.LENGTH_SHORT).show();
            else uploadFile();
        });

        process();
    }

    private void uploadFile() {
        progressBar.setVisibility(View.VISIBLE);
        if (subjectSpinner.isClickable() && semesterSpinner.isClickable() && content != null && subjectSpinner.getSelectedItemPosition() != 0 && semesterSpinner.getSelectedItemPosition() != 0) {
            String subj = ((KV)subjectSpinner.getSelectedItem()).getId();
            String sem = ((KV)semesterSpinner.getSelectedItem()).getId();
            String fileName = getFileName();
            String[] file = fileName.split("\\.");

            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
            long now = new Date().getTime();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("semesters/"+sem+"/subjects/"+subj+"/"+file[0]+now);

            reference.putFile(content).addOnSuccessListener(s -> {
                reference.getDownloadUrl().addOnSuccessListener(uri-> {
                    String url = uri.toString();
                    DataFile dataFile = new DataFile();
                    dataFile.setDownloadURL(url);
                    dataFile.setFileName(file[0]);
                    if (file.length >= 2)
                    dataFile.setFileExtension(file[file.length-1]);
                            else dataFile.setFileExtension("file");
                    firebaseFirestore.collection("semesters").document(sem).collection("subjects").document(subj).collection("data").add(dataFile).addOnSuccessListener(v->{
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                });
            }).addOnFailureListener(f->{
                Toast.makeText(this, "File isn't added", Toast.LENGTH_SHORT).show();
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Fill everything", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName() {
        String result = null;
        if (content.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(content, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
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

    private void process() {

        firebaseFirestore.collection("semesters").get().addOnSuccessListener(docs -> {
            for (DocumentSnapshot ds : docs.getDocuments()) {
                KV kv = ds.toObject(KV.class);
                kv.setId(ds.getId());
                semestersList.add(kv);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                semestersList.sort((a, b) -> (int) (a.getDateCreated() - b.getDateCreated()));
            }
            progressBar.setVisibility(View.GONE);
            semesterAdapter.notifyDataSetChanged();
        });


        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    KV kv = (KV) parent.getItemAtPosition(position);
                    subjectsList.clear();
                    subjectsList.add(defKv);
                    subjectSpinner.setClickable(false);
                    firebaseFirestore.collection("semesters").document(kv.getId()).collection("subjects").get().addOnSuccessListener(docs -> {
                        for (DocumentSnapshot ds : docs.getDocuments()) {
                            KV k = ds.toObject(KV.class);
                            Toast.makeText(AdminUpload.this, k.getKey(), Toast.LENGTH_SHORT).show();
                            k.setId(ds.getId());
                            subjectsList.add(k);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            subjectsList.sort((a, b) -> (int) (a.getDateCreated() - b.getDateCreated()));
                        }
                        progressBar.setVisibility(View.GONE);
                        subjectsAdapter.notifyDataSetChanged();
                        subjectSpinner.setClickable(true);
                    });
                } else {
                    subjectsList.clear();
                    subjectsList.add(defKv);
                    subjectSpinner.setClickable(false);
                    subjectsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            content = data.getData();
        }
    }
}
