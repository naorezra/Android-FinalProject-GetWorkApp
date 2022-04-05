package com.shiranaor.GetWork.Activities;

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

import androidx.appcompat.app.AppCompatActivity;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.Gallery.ChoosePhotosActivity;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.shiranaor.GetWork.Activities.Activity_Employee.DIPLOMA_IMAGE;
import static com.shiranaor.GetWork.Activities.Activity_Employee.UPLOADS_ID;
import static com.shiranaor.GetWork.Activities.Activity_Employee.USER_IMAGE;
import static com.shiranaor.GetWork.MyFireBase.FireBase_RealTime.calculatePosition;
import static com.shiranaor.GetWork.MyFireBase.FireBase_RealTime.deleteImage;

// This page allows a employee to update his data
public class Activity_EmployeeUpdateDetails extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private EditText firstName;
    private EditText lastName;
    private EditText businessName;
    private EditText phoneNumber;
    private Spinner location;
    private Spinner subject;
    private TextView email;
    private Button update;
    private Button delete;
    private Button galleryButton;
    private Button diplomaButton;
    private ImageView image;
    // to help define which image loader is now on action - the diploma or the user profile
    private int currImagePickerMode;
    // for employee images paths
    private String diplomaUri = "";
    private String profileUri = "";
    private String uploadsId = "";

    private String oldImage = "";
    // Uri indicates, where the image will be picked from
    private Uri filePath = Uri.parse("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__employee_update_details);
        findViews();
        initViews();
    }

    private void initViews() {

        // Register the image view and the text view to choose images
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currImagePickerMode = USER_IMAGE;
                SelectImage();
            }
        });

        // changes the diploma image
        diplomaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currImagePickerMode = DIPLOMA_IMAGE;
                SelectImage();
            }
        });

        // get all the employee data from firebase
        FireBase_RealTime.readEmployeeById(FireBase_Auth.getUserId(), new Callback_ReadData() {

            @Override
            public void success(Object data) {
                final Employee employee = (Employee) data;

                // init the cities spinner
                FireBase_RealTime.readCities(new Callback_ReadData() {
                    @Override
                    public void success(Object data) {

                        ArrayAdapter myAdapter = new ArrayAdapter(Activity_EmployeeUpdateDetails.this, android.R.layout.simple_spinner_item, (List<String>) data);
                        location.setAdapter(myAdapter);
                        location.setSelection(calculatePosition((ArrayList<String>) data, employee.getLocation()));
                    }

                    @Override
                    public void failed(String message) {

                    }
                });

                // init the subjects spinner
                FireBase_RealTime.readSubjects(new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        ArrayAdapter myAdapter = new ArrayAdapter(Activity_EmployeeUpdateDetails.this, android.R.layout.simple_spinner_item, (List<String>) data);
                        subject.setAdapter(myAdapter);
                        subject.setSelection(calculatePosition((ArrayList<String>) data, employee.getSubject()));
                    }

                    @Override
                    public void failed(String message) {

                    }
                });

                // sets the value from firebase to the form
                firstName.setText(employee.getFirstName());
                lastName.setText(employee.getLastName());
                phoneNumber.setText(employee.getPhoneNumber());
                businessName.setText(employee.getBusinessName());
                email.setText(employee.getEmail());

                if (employee.hasProfilePicture()) {
                    Picasso.get().load(Uri.parse(employee.getImage())).into(image);
                    oldImage = employee.getImage();
                }

                delete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        FireBase_RealTime.deleteEmployee(employee, new Callback_Success() {
                            @Override
                            public void success(String message) {
                                Toast.makeText(Activity_EmployeeUpdateDetails.this, "העובד נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(Activity_EmployeeUpdateDetails.this, Activity_Menu.class));
                            }

                            @Override
                            public void failed(String message) {
                                Toast.makeText(Activity_EmployeeUpdateDetails.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // what happens when employee press on update
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkFields()) {
                            Toast.makeText(Activity_EmployeeUpdateDetails.this, "נא למלא את כל הפרטים", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        employee.setFirstName(firstName.getText().toString().trim());
                        employee.setLastName(lastName.getText().toString().trim());
                        employee.setPhoneNumber(phoneNumber.getText().toString().trim());
                        employee.setLocation(location.getSelectedItem().toString().trim());
                        employee.setBusinessName(businessName.getText().toString().trim());
                        employee.setSubject(subject.getSelectedItem().toString().trim());

                        FireBase_RealTime.updateEmployee(employee, new Callback_Success() {
                            @Override
                            public void success(String message) {
                                // if some image was changed
                                if (diplomaUri.length() > 0) {
                                    FireBase_RealTime.updateEmployeeImage(DIPLOMA_IMAGE, employee, diplomaUri);
                                }
                                if (profileUri.length() > 0) {
                                    if(oldImage.length() > 0 ) {
                                        deleteImage(oldImage);
                                    }
                                    FireBase_RealTime.updateEmployeeImage(USER_IMAGE, employee, profileUri);
                                }
                                Toast.makeText(Activity_EmployeeUpdateDetails.this, "הפרטים עודכנו בהצלחה!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void failed(String message) {
                                Toast.makeText(Activity_EmployeeUpdateDetails.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                // let the employee to edit his gallery
                galleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_EmployeeUpdateDetails.this, ChoosePhotosActivity.class);
                        intent.putExtra(UPLOADS_ID, employee.getUploadsId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void failed(String message) {

            }
        });


    }

    // validate form
    private boolean checkFields() {
        return !(
                        firstName.getText().toString().trim().isEmpty() ||
                        lastName.getText().toString().trim().isEmpty() ||
                        phoneNumber.getText().toString().trim().isEmpty() ||
                        location.getSelectedItem().toString().trim().isEmpty() ||
                        businessName.getText().toString().trim().isEmpty() ||
                        subject.getSelectedItem().toString().trim().isEmpty()
        );
    }

    private void findViews() {
        firstName = findViewById(R.id.employeeUpdateDetails_EditText_firstName);
        lastName = findViewById(R.id.employeeUpdateDetails_EditText_lastName);
        phoneNumber = findViewById(R.id.employeeUpdateDetails_EditText_phoneNumber);
        location = findViewById(R.id.employeeUpdateDetails_Spinner_location);
        email = findViewById(R.id.employeeUpdateDetails_TextView_email);
        businessName = findViewById(R.id.employeeUpdateDetails_EditText_businessName);
        subject = findViewById(R.id.employeeUpdateDetails_Spinner_subject);
        update = findViewById(R.id.employeeUpdateDetails_Button_update);
        delete = findViewById(R.id.employeeUpdateDetails_Button_delete);
        galleryButton = findViewById(R.id.employeeUpdateDetails_galleryButton);
        image = findViewById(R.id.employeeUpdateDetails_image);
        diplomaButton = findViewById(R.id.employeeUpdateDetails_diplomaButton);
    }

    // Select Image method
    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Image from here..."),
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
                diplomaButton.setText("תמונה נבחרה");
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
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        }
    }
}