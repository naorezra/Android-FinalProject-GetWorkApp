package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.data.model.ChatMessage;
import com.shiranaor.GetWork.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.UUID;
// This page shows a single chat conversation based on the intent data which sent to this activity.
public class Activity_ChatConversation extends AppCompatActivity {
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    public static String PARTNER_ID = "partnerId";
    public static String CHAT_ID = "chatId";

    // this is the adapter of each message
    private FirebaseListAdapter<ChatMessage> adapter;
    // data about who data is with
    String partnerId = "";
    String chatId = "";

    private EditText textMessageInput;
    private FloatingActionButton sendMessage;
    private FloatingActionButton sendImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        readExtras();
        initViews();
        displayChatMessages();

        // What happens when clicking on send chat
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMessage(textMessageInput.getText().toString(), "");
            }
        });

        // What happens when clicking on send image
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }

    // Get all intent data
    private void readExtras() {
        Intent intent = getIntent();
        partnerId = intent.getStringExtra(PARTNER_ID);
        chatId = intent.getStringExtra(CHAT_ID);
    }

    // Initilaize the views
    private void initViews() {
        textMessageInput = (EditText) findViewById(R.id.input);
        sendMessage = (FloatingActionButton) findViewById(R.id.sendMessage);
        sendImage = (FloatingActionButton) findViewById(R.id.addImage);
    }

    // This functions updated the chat messages based on firebase
    public void displayChatMessages() {
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference("chats/" + getChatId())) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                ImageView messageImage = (ImageView) v.findViewById(R.id.messageImg);
                // Reset picaso image every rendering
                Picasso.get().cancelRequest(messageImage);
                // Set the content
                if (model.getMessageText().length() > 0) {
                    messageText.setText(model.getMessageText());
                }
                if (model.getImgSrc().length() > 0) {
                    Picasso.get().load(Uri.parse(model.getImgSrc())).resize(700, 700).into(messageImage);
                }
                messageUser.setText(model.getMessageUser());
                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }

    // Returns this chat id
    public String getChatId() {
        return generateChatId(FirebaseAuth.getInstance()
                .getCurrentUser().getUid(), partnerId);
    }

    // Generates chat id based on two id's.
    public static String generateChatId(String user1, String user2) {
        String[] ids = {user1, user2};
        // Sort the id to get the exact chatID everyTime
        Arrays.sort(ids);
        // The chat Id is id1_id2
        return ids[0] + "_" + ids[1];
    }

    // Select Image method
    private void selectImage() {
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
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            FireBase_RealTime.uploadChatImage(filePath.toString(),
                    UUID.randomUUID().toString(),
                    new Callback_Success() {
                        @Override
                        public void success(String message) {
                            // The message from the success callback is the image uri on firebase
                            sendNewMessage("", message);
                        }
                        @Override
                        public void failed(String message) {
                        }
                    });
        }
    }

    private void sendNewMessage(String message, String imgSrc) {
        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
                .getReference("chats/" + getChatId())
                .push()
                .setValue(new ChatMessage(message, FirebaseAuth.getInstance().getCurrentUser().getEmail(), imgSrc)
                );
        // Clear the input
        textMessageInput.setText("");
    }
}