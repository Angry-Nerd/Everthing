package com.example.akshiban.everything.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Semester extends AppCompatActivity {


    Spinner semesterSpinner, subjectSpinner;
    FirebaseFirestore firebaseFirestore;
    ArrayList<KV> subjectsList, semestersList;
    private SpinnerAdapter semesterAdapter, subjectsAdapter;
    private ProgressBar progressBar;
    KV defKv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);


        firebaseFirestore = FirebaseFirestore.getInstance();
        semesterSpinner = findViewById(R.id.semester_spinner);
        subjectSpinner = findViewById(R.id.subject_spinner);
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

        semesterAdapter = new SpinnerAdapter(this, semestersList);
        subjectsAdapter = new SpinnerAdapter(this, subjectsList);

        progressBar = findViewById(R.id.semester_progress);

        semesterSpinner.setAdapter(semesterAdapter);
        subjectSpinner.setAdapter(subjectsAdapter);
        subjectSpinner.setClickable(false);


        process();


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
                            Toast.makeText(Semester.this, k.getKey(), Toast.LENGTH_SHORT).show();
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


//        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                KV kv = (KV) parent.getItemAtPosition(position);
//
//                firebaseFirestore.collection("semesters").document(kv.getId()).collection("subjects").
//                        document(kv.getId()).collection("data").get().addOnSuccessListener(docs -> {
//
//                });
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

    }

    public void fetchData(View view) {
        if (subjectSpinner.isClickable() && semesterSpinner.isClickable() && subjectSpinner.getSelectedItemPosition() != 0 && semesterSpinner.getSelectedItemPosition() != 0) {
            Intent intent = new Intent(this, DataView.class);
            intent.putExtra("sem", ((KV) semesterSpinner.getSelectedItem()).getId());
            intent.putExtra("subj", ((KV) subjectSpinner.getSelectedItem()).getId());
            startActivity(intent);
        }

    }
}
