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

//Class for connecting as an employee or move to sign up
public class Activity_SignIn_Employee extends AppCompatActivity {

    //Details of a professional
    private Button registerEmployee;
    private EditText emailEmployee;
    private Button loginEmployee;
    private EditText passwordEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_employee);
        findViews();
        initViews();


    }

    private void initViews() {
        registerEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_SignIn_Employee.this, Activity_Employee.class);
                startActivity(intent);
            }
        });
        loginEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cheack if the fields not empty
                if (!checkFields()) {
                    Toast.makeText(Activity_SignIn_Employee.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }
                FireBase_Auth.login(emailEmployee.getText().toString(), passwordEmployee.getText().toString(), Activity_SignIn_Employee.this, new Callback_Success() {
                    @Override
                    public void success(String message) {//the loginIn succeeded
                        Toast.makeText(Activity_SignIn_Employee.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_SignIn_Employee.this, Activity_Employee_InterFace.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void failed(String message) {//In anthor case return error
                        if(message.contains(" no user record ")) {
                            Toast.makeText(Activity_SignIn_Employee.this, "משתמש לא קיים", Toast.LENGTH_SHORT).show();
                        }
                        else if(message.contains(" The password is invalid")) {
                            Toast.makeText(Activity_SignIn_Employee.this, "סיסמה שגויה", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Activity_SignIn_Employee.this, "שגיאה בהתחברות", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private boolean checkFields() {//An auxiliary function for checking that the fields are not empty
        if (emailEmployee.getText().toString().trim().isEmpty() ||
                passwordEmployee.getText().toString().trim().isEmpty()
        )
            return false;
        return true;
    }

    private void findViews() {
        registerEmployee = findViewById(R.id.signin_Button_register_employee);
        emailEmployee = findViewById(R.id.signin_EditText_email_employee);
        loginEmployee = findViewById(R.id.signin_Button_login_employee);
        passwordEmployee = findViewById(R.id.signin_EditText_password_employee);
    }
}