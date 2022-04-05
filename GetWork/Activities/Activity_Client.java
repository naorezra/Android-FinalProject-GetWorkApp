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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Client;

import java.io.IOException;
import java.util.List;
// This page shows a client sign up page.
public class Activity_Client extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // Uri indicates, where the image will be picked from
    private Uri filePath=Uri.parse("");
    private Button client_Button_register;//Confirmation button
    //customer details
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private Spinner location;
    private EditText email;
    private EditText password;
    // view for image view
    private ImageView client_ImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        findViews();
        initViews();
    }

    // Search the views on the resources.
    private void findViews() {
        client_Button_register = findViewById(R.id.client_Button_register);
        firstName = findViewById(R.id.client_EditText_firstName);
        lastName = findViewById(R.id.client_EditText_lastName);
        phoneNumber = findViewById(R.id.client_EditText_phoneNumber);
        location = findViewById(R.id.client_Spinner_location);
        email = findViewById(R.id.client_EditText_email);
        password = findViewById(R.id.client_EditText_password);
        client_ImageView = findViewById(R.id.client_ImageView);
    }

    // Initialize the views we searched earlier.
    private void initViews() {
        FireBase_RealTime.readCities(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter myAdapter = new ArrayAdapter(Activity_Client.this, android.R.layout.simple_spinner_item, (List<String>) data);
                location.setAdapter(myAdapter);
            }
            @Override
            public void failed(String message) {
            }
        });


        client_Button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClient();
            }
        });

        // Register the image view to choose image
        client_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
    }

    // Register the client to the system.
    private void registerClient() {
        //Function to check if all fields are full
        if (!checkFields()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        // Using firebase class to register.
        FireBase_Auth.register(email.getText().toString().trim(), password.getText().toString().trim(), this, new Callback_Success() {
            @Override
            public void success(String message) {
                Client client = new Client(firstName.getText().toString().trim(),
                        lastName.getText().toString().trim(),
                        location.getSelectedItem().toString().trim(),
                        filePath.toString(),
                        phoneNumber.getText().toString().trim(),
                        email.getText().toString().trim(),
                        FireBase_Auth.getUserId());
                FireBase_RealTime.createNewClient(client, new Callback_Success() {
                    @Override
                    public void success(String message) {
                        Toast.makeText(Activity_Client.this, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failed(String message) {
                        //If something fails in the registration process the function returns an error
                        Toast.makeText(Activity_Client.this, "אחד מהפרטים לא נכונים בדוק שנית", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void failed(String message) {

                Toast.makeText(Activity_Client.this, "אחד מהפרטים לא נכונים בדוק שנית", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Validates the form data
    private boolean checkFields() {
        //Function return true if the fields are empty else return fasle.
        if (client_Button_register.getText().toString().trim().isEmpty() ||
                firstName.getText().toString().trim().isEmpty() ||
                lastName.getText().toString().trim().isEmpty() ||
                phoneNumber.getText().toString().trim().isEmpty() ||
                location.getSelectedItem().toString().trim().isEmpty() ||
                email.getText().toString().trim().isEmpty() ||
                password.getText().toString().trim().isEmpty()
        )
            return false;
        return true;
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
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                client_ImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}
