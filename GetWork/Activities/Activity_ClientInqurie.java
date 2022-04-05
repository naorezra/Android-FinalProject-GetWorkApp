package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.shiranaor.GetWork.Adapters.Adapter_Employee;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;
import com.shiranaor.GetWork.data.model.Inquirie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shiranaor.GetWork.Activities.Activity_MyInquries.IS_CLIENT_EXTRA;
import static com.shiranaor.GetWork.MyFireBase.FireBase_RealTime.calculatePosition;

//class for  a request opened by a customer
public class Activity_ClientInqurie extends AppCompatActivity {
    //Inquire details
    public static final String INQUIRIE_ID_INTENT_EXTRA = "INQUIRIE_ID_INTENT_EXTRA";
    private Spinner subject;
    private Spinner location;
    private EditText description;
    private TextView date;
    private Button update;
    private Button delete;
    private ListView listOfEmpls;
    private TextView head;
    private String inquirieId;
    private boolean isClient;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_inqurie);
        findViews();
        readExtras();
        setViews();
    }

    // Read the data from firebase into all of the views
    private void setViews() {
        FireBase_RealTime.readInquirieById(inquirieId, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                final Inquirie inquirie = (Inquirie) data;
                // cities spinner
                FireBase_RealTime.readCities(new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        ArrayAdapter myAdapter = new ArrayAdapter(Activity_ClientInqurie.this, android.R.layout.simple_spinner_item, (List<String>) data);
                        location.setAdapter(myAdapter);
                        location.setSelection(calculatePosition((ArrayList<String>) data, inquirie.getLocation()));
                    }
                    @Override
                    public void failed(String message) {
                    }
                });

                // spinner subjects
                FireBase_RealTime.readSubjects(new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        ArrayAdapter myAdapter = new ArrayAdapter(Activity_ClientInqurie.this, android.R.layout.simple_spinner_item, (List<String>) data);
                        subject.setAdapter(myAdapter);
                        subject.setSelection(calculatePosition((ArrayList<String>) data, inquirie.getSubject()));
                    }
                    @Override
                    public void failed(String message) {
                    }
                });

                // Handle the candidates state
                if (inquirie.getSelectedEmployee().length() > 0) {
                    head.setText("בחרת בבעל המקצוע");
                    initListViewOfEmp(inquirie.getCandidates(), "");
                } else {
                    initListViewOfEmp(inquirie.getCandidates(), inquirie.getId());
                }
                description.setText(inquirie.getDescription());
                date.setText(inquirie.getDate());
                // what happend when clicks update
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkFields()) {
                            Toast.makeText(Activity_ClientInqurie.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        inquirie.setSubject(subject.getSelectedItem().toString().trim());
                        inquirie.setLocation(location.getSelectedItem().toString().trim());
                        inquirie.setDescription(description.getText().toString().trim());
                        FireBase_RealTime.updateInquirie(inquirie, new Callback_Success() {
                            @Override
                            public void success(String message) {
                                Toast.makeText(Activity_ClientInqurie.this, "הפרטים עודכנו בהצלחה!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            @Override
                            public void failed(String message) {
                                Toast.makeText(Activity_ClientInqurie.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

                // handle delete
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uploads/" + inquirie.getUploadsId());
                        reference.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Remove all from storage
                                        HashMap<String, HashMap<String, Object>> value = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                                        if(value != null){
                                            for (HashMap<String, Object> image : value.values()) {
                                                FirebaseStorage.getInstance().getReferenceFromUrl(image.get("imageUrl").toString()).delete();
                                            }
                                        }

                                        //Remove all from real time database
                                        FireBase_RealTime.deleteInquirie(inquirie.getId(), new Callback_Success() {
                                            @Override
                                            public void success(String message) {
                                                // if its deleted so need to delete her album
                                                FireBase_RealTime.deleteUpload(inquirie.getUploadsId(), "", new Callback_Success() {
                                                    @Override
                                                    public void success(String message) {

                                                    }

                                                    @Override
                                                    public void failed(String message) {

                                                    }
                                                });
                                                Toast.makeText(Activity_ClientInqurie.this, "הפניה נמחקה בהצלחה!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void failed(String message) {
                                                Toast.makeText(Activity_ClientInqurie.this, "אירעה שגיאה", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //handle databaseError
                                    }
                                });
                    }
                });

                // delete buttons which employee should not see
                if (!isClient) {
                    delete.setVisibility(View.GONE);
                    update.setVisibility(View.GONE);
                    head.setVisibility(View.INVISIBLE);
                    listOfEmpls.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void failed(String message) {

            }
        });

    }

    // initalize the list of the employees
    private void initListViewOfEmp(ArrayList<String> ids, String spesificInquirieId) {
        FireBase_RealTime.readEmployeesByIds(ids, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Employee> employeeArrayList = (ArrayList<Employee>) data;
                Adapter_Employee adapter_employee = new Adapter_Employee(employeeArrayList, Activity_ClientInqurie.this, spesificInquirieId);
                listOfEmpls.setAdapter(adapter_employee);
            }

            @Override
            public void failed(String message) {

            }
        });
    }

    // Validates the form data
    private boolean checkFields() {
        return !(
                subject.getSelectedItem().toString().trim().isEmpty() ||
                        location.getSelectedItem().toString().trim().isEmpty() ||
                        description.getText().toString().trim().isEmpty()
        );
    }

    // Get all intent data
    private void readExtras() {
        Intent intent = getIntent();
        inquirieId = intent.getStringExtra(INQUIRIE_ID_INTENT_EXTRA);
        isClient = intent.getBooleanExtra(IS_CLIENT_EXTRA, true);
    }

    // Search the views on the resources.
    private void findViews() {
        subject = findViewById(R.id.clientInqurie_Spinner_subject);
        location = findViewById(R.id.clientInqurie_Spinner_location);
        description = findViewById(R.id.clientInqurie_EditText_description);
        date = findViewById(R.id.clientInqurie_TextView_date);
        update = findViewById(R.id.clientInqurie_Button_update);
        delete = findViewById(R.id.clientInqurie_Button_delete);
        listOfEmpls = findViewById(R.id.clientInqurie_ListView_listOfEmp);
        head = findViewById(R.id.clientInqurie_TextView_head);
    }
}