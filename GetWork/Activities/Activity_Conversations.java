package com.shiranaor.GetWork.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.shiranaor.GetWork.Adapters.Adapter_Conversations;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Conversation;

import java.util.ArrayList;

import static com.shiranaor.GetWork.Activities.Activity_MyInquries.IS_CLIENT_EXTRA;
// This page shows a list of all user's conversations.
public class Activity_Conversations extends AppCompatActivity {
    // all of the user conversations
    ArrayList<Conversation> conversations = new ArrayList<>();
    // the adapter for each line of chat conversation
    Adapter_Conversations adapter_conversations;
    // the ui list
    ListView listView;
    private boolean isClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        readExtras();
        listView = findViewById(R.id.list_of_messages);
        // Get all the conversations from fire base.
        FireBase_RealTime.readAllUserConversations(FirebaseAuth.getInstance()
                .getCurrentUser().getUid(), isClient, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                // Using the adapter for the list of the conversations.
                ArrayList<Conversation> conversations = (ArrayList<Conversation>) data;
                adapter_conversations = new Adapter_Conversations(conversations, Activity_Conversations.this);
                listView.setAdapter(adapter_conversations);
            }
            @Override
            public void failed(String message) {
            }
        });
    }

    // Get all intent data
    private void readExtras() {
        Intent intent = getIntent();
        isClient = intent.getBooleanExtra(IS_CLIENT_EXTRA, false);
    }
}