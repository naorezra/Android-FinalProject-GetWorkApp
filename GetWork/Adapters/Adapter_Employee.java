package com.shiranaor.GetWork.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shiranaor.GetWork.Activities.Activity_Employee_Profile;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This adapter handles the initialize of each row in  the list view of all of the employees
public class Adapter_Employee extends BaseAdapter {

    public static final String EMPLOYEE_ID_EXTRA = "EMPLOYEE_ID_EXTRA";
    private ArrayList<Employee> employees;
    private Context mContext;
    private String inquirieId;

    public Adapter_Employee(ArrayList<Employee> employees, Context mContext, String inquirieId) {
        this.employees = employees;
        this.mContext = mContext;
        this.inquirieId = inquirieId;
    }

    @Override
    public int getCount() {
        //return employeeNames.length;
        return employees.size();
    }

    @Override
    public Object getItem(int position) {
        //return employeeNames[position];
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(R.layout.employee_list, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_Employee_Profile.class);
                intent.putExtra(EMPLOYEE_ID_EXTRA, employees.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        // init the data
        ImageView imageView = v.findViewById(R.id.employee_img);
        //set employee profile image
        if (employees.get(position).hasProfilePicture()) {
            Picasso.get().load(employees.get(position).getImage()).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.no_profile);
        }
        TextView textView = v.findViewById(R.id.employee_name);
        //set employee details
        textView.setText(employees.get(position).getBusinessName() +
                "\n" + employees.get(position).getLocation() +
                "\n" + employees.get(position).getSubject());

        //set rating to employee
        RatingBar ratingBar = v.findViewById(R.id.ratingWorker);
        if (employees.get(position).getAmountOfRaters() > 0) {
            ratingBar.setRating(employees.get(position).getRating() / employees.get(position).getAmountOfRaters());
        } else {
            ratingBar.setRating(0);
        }
        ratingBar.setIsIndicator(true);
        ratingBar.setFocusable(false);

        // shows only needed parts for the signed in user
        if (inquirieId.length() > 0) {
            v.findViewById(R.id.candidateButtons).setVisibility(View.VISIBLE);
            initCandidateButtons(v, employees.get(position).getId());
        } else {
            v.findViewById(R.id.candidateButtons).setVisibility(View.INVISIBLE);
        }
        return v;
    }


    // handles all of the logics of the candidates buttons
    private void initCandidateButtons(View root, String employeeId) {
        ((Button) root.findViewById(R.id.acceptCandidate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptCandidate(employeeId, inquirieId);
                root.findViewById(R.id.candidateButtons).setVisibility(View.INVISIBLE);

            }
        });

        ((Button) root.findViewById(R.id.removeCandidate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCandidate(employeeId, inquirieId);
                root.findViewById(R.id.candidateButtons).setVisibility(View.INVISIBLE);
            }
        });
    }
    //fucn accept candidate
    private void acceptCandidate(String employeeId, String inquirieId) {
        Map<String, Object> thingsToChange = new HashMap<>();
        // Reset candidates array on inquiry
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add(employeeId);
        thingsToChange.put("candidates", candidates);
        // Changes the selectEmployee
        thingsToChange.put("selectedEmployee", employeeId);
        // Edit the image value to the final uri
        FireBase_RealTime.editSpecificFields(FireBase_RealTime.INQUIRIES_PATH, inquirieId, thingsToChange);
    }
    //fucn remove candidate
    private void removeCandidate(String employeeId, String inquirieId) {
        Map<String, Object> thingsToChange = new HashMap<>();
        FireBase_RealTime.readInquiryCandidates(inquirieId, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<String> employees = (ArrayList<String>) data;
                // Remove spesific id from the list
                employees.remove(employeeId);
                thingsToChange.put("candidates", employees);
                FireBase_RealTime.editSpecificFields(FireBase_RealTime.INQUIRIES_PATH, inquirieId, thingsToChange);
            }
            @Override
            public void failed(String message) {

            }
        });
    }
}
