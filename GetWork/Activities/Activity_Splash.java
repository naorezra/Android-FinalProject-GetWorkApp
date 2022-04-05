package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;

import static java.lang.Thread.sleep;
// this page shows when all data is loaded on app
public class Activity_Splash extends AppCompatActivity {

    Animation topAnim, bottomAnim ;
    ImageView image;
    TextView  logo , slogan ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);


        image =  findViewById(R.id.imageView22);
        logo = findViewById(R.id.textView3);
        slogan = findViewById(R.id.textView4);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    FireBase_RealTime.isEmployee(new Callback_ReadData() {
                        @Override
                        public void success(Object data) {
                            Boolean isEmployee = (Boolean) data;
                            if(!isEmployee)
                            {
                                startActivity(new Intent(Activity_Splash.this, Activity_Menu.class));
                                finish();
                                return;
                            }
                            else
                            {
                                openEmployeeInterface();
                            }

                        }
                        @Override
                        public void failed(String message) {
                        }
                    });

                }

            }
        }).start();
    }
    private void openEmployeeInterface() {
        Intent intent = new Intent(this, Activity_Employee_InterFace.class);
        startActivity(intent);
    }

}