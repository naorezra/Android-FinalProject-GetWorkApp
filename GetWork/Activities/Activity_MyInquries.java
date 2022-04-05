package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.shiranaor.GetWork.Adapters.Adapter_ClientInquirie;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Inquirie;

import java.util.ArrayList;
// This page shows the client his own inquries
public class Activity_MyInquries extends AppCompatActivity {

    public static String IS_CLIENT_EXTRA = "IS_CLIENT_EXTRA";//It client or has a profession
    public static String SUBJECT_EXTRA = "SUBJECT_EXTRA";//if its profession, we save his subject.

    private ListView myInquries_ListView_listView;
    private boolean isClient;//true if is client
    private String subject;//if is client save his subject
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_inquries);
        readIntent();
        findViews();
    }

    // get all data from last activity
    private void readIntent() {
        Intent intent = getIntent();
        isClient = intent.getBooleanExtra(IS_CLIENT_EXTRA,true);
        subject = intent.getStringExtra(SUBJECT_EXTRA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {
        initListView();
    }//initviews its purpose is to put content on display

    // filters the inquiries to show only this client inquiries
    private ArrayList<Inquirie> getMyInuiries(ArrayList<Inquirie> inquirieArrayList)//Its purpose is to read all referrals from the database - in case it is a connected customer it will display all its referrals, in Markara and it works - it will show all referrals in this employee's domain
    {
        ArrayList<Inquirie> result = new ArrayList<>();

        for(Inquirie inquirie : inquirieArrayList)
        {
            if(isClient) {
                if (inquirie.getUserId().equals(FireBase_Auth.getUserId()))
                    result.add(inquirie);
            }
            else
            {
                if (inquirie.getSubject().equals(subject))
                    result.add(inquirie);
            }
        }
        return result;
    }

    // handles all of the list view of inquires technical staff
    private void initListView() {
        FireBase_RealTime.readAllInquiries(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Inquirie> inquirieArrayList = (ArrayList<Inquirie>) data;
                //add filter
                    inquirieArrayList = getMyInuiries(inquirieArrayList);
                ListView yourListView = (ListView) findViewById(R.id.myInquries_ListView_listView);
                // get data from the table by the ListAdapter
                Adapter_ClientInquirie customAdapter = new Adapter_ClientInquirie(Activity_MyInquries.this, R.layout.item_client_inqurie,inquirieArrayList,isClient);
                yourListView .setAdapter(customAdapter);
            }
            @Override
            public void failed(String message) {
            }
        });
    }

    private void findViews() {
        myInquries_ListView_listView = findViewById(R.id.myInquries_ListView_listView);
    }
}