package com.shiranaor.GetWork.Adapters;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiranaor.GetWork.Activities.Activity_ClientInqurie;
import com.shiranaor.GetWork.Gallery.ChoosePhotosActivity;
import com.shiranaor.GetWork.Gallery.ImagesActivity;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Inquirie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.shiranaor.GetWork.Activities.Activity_ClientInqurie.INQUIRIE_ID_INTENT_EXTRA;
import static com.shiranaor.GetWork.Activities.Activity_Employee.UPLOADS_ID;
import static com.shiranaor.GetWork.Activities.Activity_MyInquries.IS_CLIENT_EXTRA;

// This adapter handles the initialize of each row in  the list view of all of the inquiries
public class Adapter_ClientInquirie extends ArrayAdapter<Inquirie> {

    private int resourceLayout;
    private Context mContext;
    private boolean isClient;

    public Adapter_ClientInquirie(Context context, int resource, List<Inquirie> items, boolean isClient) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.isClient = isClient;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }
        Inquirie inquirie = getItem(position);
        if (inquirie != null) {
            // init the data
            TextView itemClientInqurie_TextView_subject = (TextView) v.findViewById(R.id.itemClientInqurie_TextView_subject);
            TextView itemClientInqurie_TextView_location = (TextView) v.findViewById(R.id.itemClientInqurie_TextView_location);
            Button itemClientInqurie_Button_show = (Button) v.findViewById(R.id.itemClientInqurie_Button_show);
            Button itemClientInqurie_Button_handle = (Button) v.findViewById(R.id.itemClientInqurie_Button_handle);
            ImageView itemClientInqurie_ImageView_image = (ImageView) v.findViewById(R.id.itemClientInqurie_ImageView_image);
            if (itemClientInqurie_TextView_subject != null) {
                itemClientInqurie_TextView_subject.setText(inquirie.getSubject());
            }
            if (itemClientInqurie_TextView_location != null) {
                itemClientInqurie_TextView_location.setText(inquirie.getLocation());
            }

            // this intent is made to open the images of this inquiry
            Intent inquirieGalleryIntent;
            boolean isEmployeeInquiry = inquirie.getUserId().equals(FireBase_Auth.getUserId());
            if (isEmployeeInquiry) {
                inquirieGalleryIntent = new Intent(mContext, ChoosePhotosActivity.class);
                // Hide button if this is a client not an employee
                itemClientInqurie_Button_handle.setVisibility(View.INVISIBLE);
            } else {
                inquirieGalleryIntent =new Intent(mContext, ImagesActivity.class);
                itemClientInqurie_Button_handle.setVisibility(View.VISIBLE);
                handleEmployeeCandidate(itemClientInqurie_Button_handle, inquirie);
            }

            handleEmptyGallery(inquirieGalleryIntent, inquirie);

            itemClientInqurie_ImageView_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(inquirieGalleryIntent);
                }
            });
            if (itemClientInqurie_Button_show != null) {
                itemClientInqurie_Button_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, Activity_ClientInqurie.class);
                        i.putExtra(INQUIRIE_ID_INTENT_EXTRA, inquirie.getId());
                        i.putExtra(IS_CLIENT_EXTRA, isClient);
                        mContext.startActivity(i);
                    }
                });
            }
            if (itemClientInqurie_Button_handle != null) {
                itemClientInqurie_Button_handle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> thingsToChange = new HashMap<>();
                        inquirie.getCandidates().add(FireBase_Auth.getUserId().toString());
                        thingsToChange.put("candidates", inquirie.getCandidates());
                        FireBase_RealTime.editSpecificFields(FireBase_RealTime.INQUIRIES_PATH, inquirie.getId(), thingsToChange);
                        handleEmployeeCandidate((Button) view, inquirie);
                    }
                });
            }
        }

        return v;
    }

    // this function will initialize a gallery to inquiry if not exists.
    public void handleEmptyGallery(Intent intent, Inquirie inquirie) {
        if (inquirie.getUploadsId().length() > 0) {
            intent.putExtra(UPLOADS_ID, inquirie.getUploadsId());
        } else {
            String id = UUID.randomUUID().toString();
            intent.putExtra(UPLOADS_ID, id);
            Map<String, Object> thingsToChange = new HashMap<>();
            thingsToChange.put("uploadsId", id);
            FireBase_RealTime.editSpecificFields(FireBase_RealTime.INQUIRIES_PATH, inquirie.getId(), thingsToChange);
        }
    }

    // what happened when a candidate wants to sign this inquiry
    private void handleEmployeeCandidate(Button button, Inquirie inquirie) {
        String employeeId = FireBase_Auth.getUserId();
        if (inquirie.getSelectedEmployee().length() > 0 && !inquirie.getSelectedEmployee().equals(employeeId)) {
            button.setTextColor(RED);
            button.setEnabled(false);
            button.setText("הפנייה נתפסה");
        } else if (inquirie.getSelectedEmployee().equals(employeeId)) {
            button.setTextColor(GREEN);
            button.setEnabled(false);
            button.setText("נבחרת לפניה");
        } else if (inquirie.getCandidates().contains(FireBase_Auth.getUserId())) {
            button.setTextColor(BLUE);
            button.setEnabled(false);
            button.setText("ממתין לאישור הלקוח");
        }
    }
}