package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;

import static com.shiranaor.GetWork.Activities.Activity_MyInquries.IS_CLIENT_EXTRA;
import static com.shiranaor.GetWork.Activities.Activity_MyInquries.SUBJECT_EXTRA;
import static com.shiranaor.GetWork.Adapters.Adapter_Employee.EMPLOYEE_ID_EXTRA;

// This page shows an interface for signed in employee
public class Activity_Employee_InterFace extends AppCompatActivity {
    private Button myInquiries;
    private Button massage;
    private Button logout;
    private Button editDetials;
    private Button myCard;
    private Button aboutPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__employee__inter_face);
        findViews();
        initViews();
    }

    // open a list of inquiries which are the same subject as this employee.
    private void openMyInquiries() {
        FireBase_RealTime.readEmployeeSubject(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_MyInquries.class);
                intent.putExtra(IS_CLIENT_EXTRA, false);
                intent.putExtra(SUBJECT_EXTRA, data.toString());
                startActivity(intent);
            }

            @Override
            public void failed(String message) {

            }
        });

    }

    // Initalize the views with data
    private void initViews() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBase_Auth.logout();
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_Menu.class);
                startActivity(intent);
                finish();
            }
        });
        // my queries button click
        myInquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyInquiries();
            }
        });
        // my messages button click
        massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_Conversations.class);
                intent.putExtra(IS_CLIENT_EXTRA, false);
                startActivity(intent);
            }
        });
        // about page button click
        aboutPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_AboutPage.class);
                startActivity(intent);
            }
        });
        // edit detailes click
        editDetials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_EmployeeUpdateDetails.class);
                startActivity(intent);
            }
        });
        // employee personal profile click
        myCard.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(Activity_Employee_InterFace.this, Activity_Employee_Profile.class);
                intent.putExtra(EMPLOYEE_ID_EXTRA, FireBase_Auth.getUserId());
                startActivity(intent);
            }
        });
    }

    // find the views from the resources.
    private void findViews() {
        myInquiries = findViewById(R.id.interFace_Button_myInquiries);
        massage = findViewById(R.id.interFace_Button_massage);
        logout = findViewById(R.id.interFace_Button_logout);
        editDetials = findViewById(R.id.interFace_Button_editDetials);
        myCard = findViewById(R.id.interFace_Button_myCard);
        aboutPage = findViewById(R.id.interFace_Button_aboutPage);
    }
}