package com.shiranaor.GetWork.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shiranaor.GetWork.Activities.Activity_ChatConversation;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Conversation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
// This adapter handles the initialize of each row in  the list view of all of the conversations
public class Adapter_Conversations extends BaseAdapter {
    public static final String EMPLOYEE_ID_EXTRA = "EMPLOYEE_ID_EXTRA";
    private ArrayList<Conversation> conversations;
    private Context mContext;

    public Adapter_Conversations(ArrayList<Conversation> conversations, Context mContext) {
        this.conversations = conversations;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        //return employeeNames.length;
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        //return employeeNames[position];
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    //func that show view about chat conversation
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(R.layout.chat_list, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_ChatConversation.class);
                String currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                intent.putExtra(Activity_ChatConversation.CHAT_ID, currentId);
                String partnerId = conversations.get(position).getId();
                // Extract partner id out of the conversation id
                partnerId = partnerId.replace(currentId, "");
                partnerId = partnerId.replace("_", "");

                intent.putExtra(Activity_ChatConversation.PARTNER_ID, partnerId);
                mContext.startActivity(intent);
            }
        });

        // init the data
        ImageView chatImage = v.findViewById(R.id.chat_userImg);
        if (conversations.get(position).hasImage()) {
            Picasso.get().load(Uri.parse(conversations.get(position).getImg())).into(chatImage);
        } else {
            chatImage.setImageResource(R.drawable.no_profile);
        }

        TextView deleteChat = v.findViewById(R.id.deleteChat);
        deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            //func to delete chat
            public void onClick(View view) {
                FireBase_RealTime.deleteChat(conversations.get(position).getId(), new Callback_Success() {
                    @Override
                    public void success(String message) {
                        Toast.makeText(mContext.getApplicationContext(), "צ'אט נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                        conversations.remove(position);
                        v.setVisibility(View.GONE);
                    }
                    @Override
                    public void failed(String message) {
                    }
                });
            }
        });

        TextView chat_chatName = v.findViewById(R.id.chat_chatName);
        chat_chatName.setText(conversations.get(position).getName());

        return v;
    }
}