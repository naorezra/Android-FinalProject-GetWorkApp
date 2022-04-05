package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shiranaor.GetWork.R;

// This page shows the about page of the app, including all rights
public class Activity_AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button share;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__about_page);
        share = (Button) findViewById(R.id.button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "GetWork App");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "GetWork new App in PlayStore by Naor Ezra and Shira Alon");
                shareIntent.setType("text/plan");
                startActivity(shareIntent);
            }
        });
        Button button=(Button)findViewById(R.id.mail);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),Activity_MailAboutPage.class));
            }
        });
    }
}