package com.shiranaor.GetWork.Gallery;

import static com.shiranaor.GetWork.Activities.Activity_Employee.UPLOADS_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Upload;

import java.util.ArrayList;
import java.util.List;

// This page is the gallery page like in employee profile or in inquiry
public class ImagesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private String uploadsId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        // init views
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();

        readExtras();
        //checks if have photos and add them to the upload list
        if(uploadsId != null && uploadsId.length() > 1) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/" + uploadsId);
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Upload upload = postSnapshot.getValue(Upload.class);
                        mUploads.add(upload);
                    }
                    mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            mAdapter = new ImageAdapter(ImagesActivity.this, new ArrayList<>());
            mRecyclerView.setAdapter(mAdapter);
            mProgressCircle.setVisibility(View.INVISIBLE);
            //if did not have photos invisible view
        }
    }

    // read all data from last activity
    private void readExtras() {
        Intent intent = getIntent();
        uploadsId = intent.getStringExtra(UPLOADS_ID);

    }
}