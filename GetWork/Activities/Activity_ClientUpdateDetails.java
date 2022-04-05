package com.shiranaor.GetWork.Activities;

import static com.shiranaor.GetWork.MyFireBase.FireBase_RealTime.calculatePosition;
import static com.shiranaor.GetWork.MyFireBase.FireBase_RealTime.deleteImage;

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
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Client;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This page shows a client update page.
public class Activity_ClientUpdateDetails extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private Spinner location;
    private TextView email;
    private Button update;
    private Button delete;
    private ImageView userImage;
    private String oldImage = "";

    // this is the path for client photo
    private String profileUri = "";
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_update_details);
        findViews();
        initViews();
    }

    private void initViews() {
        // gets the data from firebase
        FireBase_RealTime.readClientById(FireBase_Auth.getUserId(), new Callback_ReadData() {
            @Override
            public void success(Object data) {
                final Client client = (Client) data;
                changeImageByUser(client.getImage());
                // Register the image view and the text view to choose images
                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectImage();
                    }
                });

                profileUri = client.getImage();
                email.setText(client.getEmail());
                firstName.setText(client.getFirstName());
                lastName.setText(client.getLastName());
                phoneNumber.setText(client.getPhoneNumber());

                // Init the spinner of city
                FireBase_RealTime.readCities(new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        ArrayAdapter myAdapter = new ArrayAdapter(Activity_ClientUpdateDetails.this,
                                android.R.layout.simple_spinner_item, (List<String>) data);
                        location.setAdapter(myAdapter);
                        location.setSelection(calculatePosition((ArrayList<String>) data, client.getLocation()));
                    }

                    @Override
                    public void failed(String message) {

                    }
                });


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FireBase_RealTime.deleteClient(client, new Callback_Success() {
                            @Override
                            public void success(String message) {
                                Toast.makeText(Activity_ClientUpdateDetails.this, "המשתמש נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            @Override
                            public void failed(String message) {
                                Toast.makeText(Activity_ClientUpdateDetails.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkFields()) {
                            Toast.makeText(Activity_ClientUpdateDetails.this, "נא למלא את כל הפרטים", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        client.setFirstName(firstName.getText().toString().trim());
                        client.setLastName(lastName.getText().toString().trim());
                        client.setPhoneNumber(phoneNumber.getText().toString().trim());
                        client.setLocation(location.getSelectedItem().toString().trim());
                        client.setImage(profileUri);
                        // TODO: change image in here
//                        client.setImage(clientUpdateDetails_EditText_image.getText().toString().trim());
                        if (oldImage != null && oldImage.length() > 0 && client.getImage().equals(oldImage)) {
                            deleteImage(oldImage);
                        }
                        FireBase_RealTime.updateClient(client, new Callback_Success() {
                            @Override
                            public void success(String message) {
                                Toast.makeText(Activity_ClientUpdateDetails.this, "הפרטים עודכנו בהצלחה!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void failed(String message) {
                                Toast.makeText(Activity_ClientUpdateDetails.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }

            @Override
            public void failed(String message) {
            }
        });


    }

    // this function chages the imageview to user image on firebase
    private void changeImageByUser(String imageUri) {
        ImageView userImage = (ImageView) findViewById(R.id.clientUpdateDetails_ImageView);
        if (imageUri.length() > 0) {
            oldImage = imageUri;
            Picasso.get().load(imageUri).into(userImage);
        }
    }


    // validates the form
    private boolean checkFields() {
        return !(
                firstName.getText().toString().trim().isEmpty() ||
                        lastName.getText().toString().trim().isEmpty() ||
                        phoneNumber.getText().toString().trim().isEmpty() ||
                        location.getSelectedItem().toString().trim().isEmpty()
        );
    }

    private void findViews() {
        firstName = findViewById(R.id.clientUpdateDetails_EditText_firstName);
        lastName = findViewById(R.id.clientUpdateDetails_EditText_lastName);
        phoneNumber = findViewById(R.id.clientUpdateDetails_EditText_phoneNumber);
        location = findViewById(R.id.clientUpdateDetails_Spinner_location);
        email = findViewById(R.id.clientUpdateDetails_TextView_email);
        update = findViewById(R.id.clientUpdateDetails_Button_update);
        delete = findViewById(R.id.clientUpdateDetails_Button_delete);
        userImage = findViewById(R.id.clientUpdateDetails_ImageView);
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
            this.profileUri = filePath.toString();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


}