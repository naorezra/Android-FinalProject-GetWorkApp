package com.shiranaor.GetWork.Activities;

import static com.shiranaor.GetWork.Activities.Activity_Employee.UPLOADS_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.Gallery.ChoosePhotosActivity;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Inquirie;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// This page help the client to create a new inquiry
public class Activity_Inquiries extends AppCompatActivity {

    private Spinner inquirie_Spinner_location;
    private Spinner inquirie_Spinner_subject;
    private EditText inquirie_EditText_description;
    private Button inquirie_Button_publish;
    private Button inquirie_Button_uploadImages;
    private String uploadsId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiries);
        findViews();
        initViews();
    }

    private void initViews() {
        //init all cities from firebase to the spinner
        FireBase_RealTime.readCities(new Callback_ReadData() {
            @Override
            // spinner locations
            public void success(Object data) {
                ArrayAdapter myAdapter = new ArrayAdapter(Activity_Inquiries.this, android.R.layout.simple_spinner_item, (List<String>) data);
                inquirie_Spinner_location.setAdapter(myAdapter);
            }

            @Override
            public void failed(String message) {

            }
        });

        //init all subjects from firebase to the spinner
        FireBase_RealTime.readSubjects(new Callback_ReadData() {
            @Override
            // spinner subject
            public void success(Object data) {
                ArrayAdapter myAdapter = new ArrayAdapter(Activity_Inquiries.this, android.R.layout.simple_spinner_item, (List<String>) data);
                inquirie_Spinner_subject.setAdapter(myAdapter);
            }

            @Override
            public void failed(String message) {

            }
        });

        //func button to uploadImages open activity
        inquirie_Button_uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadsId == null || uploadsId.length() == 0) {
                    uploadsId = UUID.randomUUID().toString();
                }
                Intent intent = new Intent(Activity_Inquiries.this, ChoosePhotosActivity.class);
                intent.putExtra(UPLOADS_ID, uploadsId);
                startActivity(intent);
            }
        });

        //func button to publish inquirie and uploading it to the firebase
        inquirie_Button_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkField()) {
                    Toast.makeText(Activity_Inquiries.this, "נא למלא את כל הפרטים", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                ArrayList<String> strings = new ArrayList<>();
                strings.add("");
                Inquirie inquirie = new Inquirie(
                        FireBase_Auth.getUserId(),
                        inquirie_Spinner_location.getSelectedItem().toString().trim(),
                        inquirie_Spinner_subject.getSelectedItem().toString().trim(),
                        formatter.format(date),
                        inquirie_EditText_description.getText().toString().trim(),
                        strings,
                        "",
                        uploadsId);

                FireBase_RealTime.createNewInquirie(inquirie, new Callback_Success() {
                    @Override
                    public void success(String message) {
                        Toast.makeText(Activity_Inquiries.this, "פנייתך נרשמה", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failed(String message) {
                        Toast.makeText(Activity_Inquiries.this, "ארעה שגיאה", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
    //Check if all fields are not empty
    private boolean checkField() {
        if (
                        inquirie_Spinner_location.getSelectedItem().toString().trim().isEmpty() ||
                        inquirie_Spinner_subject.getSelectedItem().toString().trim().isEmpty() ||
                        inquirie_EditText_description.getText().toString().trim().isEmpty()
        )
            return false;
        return true;
    }

    private void findViews() {
        inquirie_Spinner_location = findViewById(R.id.inquirie_Spinner_location);
        inquirie_Spinner_subject = findViewById(R.id.inquirie_Spinner_subject);
        inquirie_EditText_description = findViewById(R.id.inquirie_EditText_description);
        inquirie_Button_publish = findViewById(R.id.inquirie_Button_publish);
        inquirie_Button_uploadImages = findViewById(R.id.inquirie_Button_uploadImages);
    }
}