package com.example.akshiban.everything.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class DataView extends AppCompatActivity {

    String sem, subj;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    List<DataFile> dataFileList;
    DataFileAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        Intent intent = getIntent();
        sem = intent.getStringExtra("sem");
        subj = intent.getStringExtra("subj");
        dataFileList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DataFileAdapter(dataFileList, this);
        recyclerView.setAdapter(adapter);

        fetch();
    }

    private void fetch() {
        firebaseFirestore.collection("semesters").document(sem).collection("subjects")
                .document(subj).collection("data").get().addOnSuccessListener(docs -> {
            for (DocumentSnapshot ds : docs.getDocuments()) {
                DataFile dataFile = ds.toObject(DataFile.class);
                dataFileList.add(dataFile);
                Toast.makeText(this, dataFile.getDownloadURL(), Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        });

    }
}
