package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.R;
// This page is for client to sign in or move to sign up page
public class Activity_SignIn_Client extends AppCompatActivity {
    private Button register;
    private EditText email;
    private Button login;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_client);
        findViews();
        initViews();


    }

    private void initViews() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_SignIn_Client.this, Activity_Client.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFields()) {
                    Toast.makeText(Activity_SignIn_Client.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }
                //check email and password
                FireBase_Auth.login(email.getText().toString(), password.getText().toString(), Activity_SignIn_Client.this, new Callback_Success() {
                    @Override
                    public void success(String message) {
                        Toast.makeText(Activity_SignIn_Client.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_SignIn_Client.this, Activity_Menu.class);
                        startActivity(intent);
                    }
                    // whats happened when user is unable to connect
                    @Override
                    public void failed(String message) {
                        if(message.contains(" no user record ")) {
                            Toast.makeText(Activity_SignIn_Client.this, "משתמש לא קיים", Toast.LENGTH_SHORT).show();
                        }
                        else if(message.contains(" The password is invalid")) {
                            Toast.makeText(Activity_SignIn_Client.this, "סיסמה שגויה", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Activity_SignIn_Client.this, "שגיאה בהתחברות", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    // validate data before trying to sign in
    private boolean checkFields() {//An auxiliary function for checking that the fields are not empty
        if (email.getText().toString().trim().isEmpty() ||
                password.getText().toString().trim().isEmpty()
        )
            return false;
        return true;
    }

    private void findViews() {
        register = findViewById(R.id.signin_Button_register);
        email = findViewById(R.id.signin_EditText_email);
        login = findViewById(R.id.signin_Button_login);
        password = findViewById(R.id.signin_EditText_password);
    }
}
