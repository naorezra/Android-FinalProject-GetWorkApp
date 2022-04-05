package com.shiranaor.GetWork.MyFireBase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shiranaor.GetWork.Callbacks.Callback_Success;

//This class is taking care of the authentication of the app with firebase
public class FireBase_Auth {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

// ...
// Initialize Firebase Auth

    public static String getUserId() {
        return mAuth.getUid();
    }
    //Receiving parameters and registering to the server
    public static void register(String email, String password, Context context, Callback_Success callback_success) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback_success.success("");
                        } else {
                            // If sign in fails, display a message to the user.
                            callback_success.failed("");
                        }
                    }
                });

    }

    //Receive an email and password and check with the server if they are correct and make a connection
    public static void login(String email, String password, Context context, Callback_Success callback_success) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback_success.success("");
                        } else {
                            // If sign in fails, display a message to the user.
                            callback_success.failed(task.getException().toString());

                        }
                    }
                });

    }


    // this function returns if any user is logger in at the moment
    public static boolean isLogedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public static void logout() {
        mAuth.signOut();
    }


}
