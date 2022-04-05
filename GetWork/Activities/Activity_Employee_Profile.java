package com.shiranaor.GetWork.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Gallery.ImagesActivity;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;
import com.squareup.picasso.Picasso;

import static com.shiranaor.GetWork.Activities.Activity_Employee.UPLOADS_ID;
import static com.shiranaor.GetWork.Adapters.Adapter_Employee.EMPLOYEE_ID_EXTRA;

// This page shows an employee profile page.
public class Activity_Employee_Profile extends AppCompatActivity {
    ImageView profilePic;
    RatingBar rating;
    TextView fullName, phone, email, cantRate, businessName, subject;
    Button startChat;
    Button profileGallery, showDiploma;

    private String id;
    private String employeeId = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        readExtras();
        findViews();
        initViews();
    }

    // Get all intent data
    private void readExtras() {
        Intent intent = getIntent();
        id = intent.getStringExtra(EMPLOYEE_ID_EXTRA);
    }

    // Initalize the views with data
    private void initViews() {
        FireBase_RealTime.readEmployeeById(id, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                Employee employee = (Employee) data;
                if (employee.hasProfilePicture()) {
                    Picasso.get().load(employee.getImage()).into(profilePic);
                }

                // Sets the id to start a conversation
                employeeId = employee.getId();
                fullName.setText(employee.getFirstName() + " " + employee.getLastName());
                phone.setText(employee.getPhoneNumber());
                email.setText(employee.getEmail());
                businessName.setText(employee.getBusinessName());
                subject.setText(employee.getSubject());

                // handles the rating calculations
                rating.setIsIndicator(false);
                if (employee.getAmountOfRaters() > 0) {
                    rating.setRating(employee.getRating() / employee.getAmountOfRaters());
                } else {
                    rating.setRating(0);
                }

                isUserAllowedToRate();

                // handles user rating
                rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        // this editor helps us save data about if user already rated
                        SharedPreferences.Editor editor = getSharedPreferences("business", MODE_PRIVATE).edit();
                        editor.putBoolean(employeeId, true);
                        editor.apply();

                        FireBase_RealTime.rateEmployee(employee, (int) rating.getRating());
                        Toast.makeText(ratingBar.getContext(), "Thanks for rating", Toast.LENGTH_SHORT);
                        rating.setIsIndicator(true);
                    }
                });

                // get to the employee profile
                profileGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openImagesActivity(employee.getUploadsId());
                    }
                });

                // opens browser to see the employee diploma
                showDiploma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(employee.getDiplomaImage()));
                        startActivity(browserIntent);
                    }
                });
            }

            @Override
            public void failed(String message) {
            }


        });

        // start chat with employee
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConversation(employeeId);
            }
        });


    }

    // find the views from the resources.
    private void findViews() {
        profilePic = findViewById(R.id.employee_profile_ImageView);
        rating = findViewById(R.id.employee_profile_rating);
        fullName = findViewById(R.id.employee_profile_name);
        phone = findViewById(R.id.employee_profile_phone);
        email = findViewById(R.id.employee_profile_email);
        startChat = findViewById(R.id.Button_startChat);
        cantRate = findViewById(R.id.cant_rate);
        profileGallery = findViewById(R.id.profileGallery);
        businessName = findViewById(R.id.employee_profile_businessName);
        subject = findViewById(R.id.employee_profile_subject);
        showDiploma = findViewById(R.id.showDiploma);
    }

    // get to the conversation
    private void startConversation(String employeeId) {
        Intent intent = new Intent(Activity_Employee_Profile.this,
                Activity_ChatConversation.class);
        intent.putExtra(Activity_ChatConversation.PARTNER_ID, employeeId);
        startActivity(intent);

    }

    // handles if signed in user can rate this employee
    private void isUserAllowedToRate() {
        // SharedPreferences is a data stored inside the phone which helps us detect if user already rated
        SharedPreferences prefs = getSharedPreferences("business", MODE_PRIVATE);

        if (!FireBase_Auth.isLogedIn()) {
            rating.setIsIndicator(true);
            rating.setFocusable(false);
            startChat.setEnabled(false);
            startChat.setText("התחבר כדי לשלוח הודעה");
        } else if (FireBase_Auth.getUserId().equals(employeeId)) {
            rating.setIsIndicator(true);
            rating.setFocusable(false);
            startChat.setVisibility(View.INVISIBLE);
            cantRate.setVisibility(View.INVISIBLE);
        } else {
            String chatId = Activity_ChatConversation.generateChatId(employeeId, FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());

            // updates the rating on firebase
            FirebaseDatabase.getInstance().getReference("chats/" + chatId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // if user already contacted this employee
                    if (snapshot.exists()) {
                        boolean isAlreadyRated = prefs.getBoolean(employeeId, false);
                        if (isAlreadyRated) {
                            cantRate.setText("כבר דירגת איש מקצוע זה");
                            rating.setIsIndicator(true);
                            rating.setFocusable(false);
                        } else {
                            cantRate.setVisibility(View.INVISIBLE);
                            rating.setFocusable(true);
                        }
                    } else {
                        rating.setIsIndicator(true);
                        rating.setFocusable(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    // go to the employee gallery
    private void openImagesActivity(String uploadsId) {
        Intent intent = new Intent(Activity_Employee_Profile.this, ImagesActivity.class);
        intent.putExtra(UPLOADS_ID, uploadsId);
        startActivity(intent);
    }
}

