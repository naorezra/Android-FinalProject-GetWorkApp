package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.Gallery.ChoosePhotosActivity;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

// this class is the page of employee sign up.
public class Activity_Employee extends AppCompatActivity {
    //Details of a professional
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private Spinner location;
    private Spinner subject;
    private EditText email;
    private EditText password;
    private EditText businessName;
    private Button register;
    private Button gallery;
    private ImageView profile;
    private TextView diploma;

    // this consts is for the image chooser
    public static final int DIPLOMA_IMAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final String UPLOADS_ID = "uploadId";

    // to help define which image loader is now on action - the diploma or the user profile
    private int currImagePickerMode;

    // all of the paths to employee images
    private String diplomaUri = "";
    private String profileUri = "";
    private String uploadsId = "";

    // Uri indicates, where the image will be picked from
    private Uri filePath=Uri.parse("");
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        findViews();
        initViews();
    }


    private void findViews() {
        firstName = findViewById(R.id.employee_EditText_firstName);
        lastName = findViewById(R.id.employee_EditText_lastName);
        phoneNumber = findViewById(R.id.employee_EditText_phoneNumber);
        location = findViewById(R.id.employee_Spinner_location);
        subject = findViewById(R.id.employee_Spinner_subject);
        email = findViewById(R.id.employee_EditText_email);
        password = findViewById(R.id.employee_EditText_password);
        register = findViewById(R.id.employee_Button_register);
        gallery = findViewById(R.id.employee_Button_gallery);
        businessName = findViewById(R.id.employee_EditText_businessName);
        profile = findViewById(R.id.employee_ImageView);
        diploma = findViewById(R.id.employee_TextView_diplomaImage);
    }

    // initialize the views
    private void initViews() {
        // Cities spinner
        FireBase_RealTime.readCities(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter myAdapter = new ArrayAdapter(Activity_Employee.this, android.R.layout.simple_spinner_item, (List<String>) data);
                location.setAdapter(myAdapter);
            }

            @Override
            public void failed(String message) {

            }
        });

        // Subjects spinner
        FireBase_RealTime.readSubjects(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter myAdapter = new ArrayAdapter(Activity_Employee.this, android.R.layout.simple_spinner_item, (List<String>) data);
                subject.setAdapter(myAdapter);
            }

            @Override
            public void failed(String message) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEmployee();
            }
        });

        // button which open a gallery for the employee
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadsId == null || uploadsId.length() == 0) {
                    uploadsId = UUID.randomUUID().toString();
                }
                Intent intent = new Intent(Activity_Employee.this, ChoosePhotosActivity.class);
                intent.putExtra(UPLOADS_ID, uploadsId);
                startActivity(intent);
            }
        });

        // Register the image view and the text view to choose images
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currImagePickerMode = USER_IMAGE;
                SelectImage();
            }
        });

        // here the employee uploads his diploma
        diploma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currImagePickerMode = DIPLOMA_IMAGE;
                SelectImage();
            }
        });

    }

    // final function - to register the employee using the form created
    private void registerEmployee() {
        if (uploadsId == null || uploadsId.length() == 0) {
            uploadsId = UUID.randomUUID().toString();
        }
        //Function to check if all fields are full
        if (!checkFields()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        FireBase_Auth.register(email.getText().toString().trim(), password.getText().toString().trim(), this, new Callback_Success() {
            @Override
            public void success(String message) {
                Employee employee = new Employee(
                        firstName.getText().toString().trim(),
                        lastName.getText().toString().trim(),
                        subject.getSelectedItem().toString().trim(),
                        location.getSelectedItem().toString(),
                        diplomaUri,
                        profileUri,
                        phoneNumber.getText().toString().trim(),
                        email.getText().toString().trim(),
                        FireBase_Auth.getUserId(),
                        businessName.getText().toString().trim(),
                        uploadsId);
                FireBase_RealTime.createNewEmployee(employee, new Callback_Success() {
                    @Override
                    public void success(String message) {
                        Toast.makeText(Activity_Employee.this, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                        FireBase_Auth.logout();
                        finish();
                    }

                    @Override
                    public void failed(String message) {
                        Toast.makeText(Activity_Employee.this, "אירעה תקלה ברישום נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void failed(String message) {
                Toast.makeText(Activity_Employee.this, "אירעה תקלה ברישום נסה מאוחר יותר", Toast.LENGTH_SHORT).show();


            }
        });
    }

    // validates the data
    private boolean checkFields() {
        return !(register.getText().toString().trim().isEmpty() ||
                firstName.getText().toString().trim().isEmpty() ||
                lastName.getText().toString().trim().isEmpty() ||
                phoneNumber.getText().toString().trim().isEmpty() ||
                location.getSelectedItem().toString().trim().isEmpty() ||
                this.diplomaUri.isEmpty() ||
                email.getText().toString().trim().isEmpty() ||
                password.getText().toString().trim().isEmpty() ||
                subject.getSelectedItem().toString().isEmpty() ||
                businessName.getText().toString().isEmpty()
        );
    }

    // Select Image method
    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            if (currImagePickerMode == DIPLOMA_IMAGE) {
                this.diplomaUri = filePath.toString();
                diploma.setText("תמונה נבחרה");
            } else {
                this.profileUri = filePath.toString();
                try {
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    profile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        }
    }
}
