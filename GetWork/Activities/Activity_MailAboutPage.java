package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.shiranaor.GetWork.R;

// This page help the user to contact us
public class Activity_MailAboutPage extends AppCompatActivity {
        EditText textTo;
        EditText textSubject;
        EditText textMessage;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_activit__mail_about_page);
            textTo = findViewById(R.id.edit_text_to);
            textSubject = findViewById(R.id.edit_text_subject);
            textMessage = findViewById(R.id.edit_text_message);
            Button buttonSend = findViewById(R.id.button_send);
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMail();
                }
            });
        }
        private void sendMail() {
            String recipientList = textTo.getText().toString();
            String[] recipients = recipientList.split(",");
            String subject = textSubject.getText().toString();
            String message = textMessage.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client"));
        }
    }

