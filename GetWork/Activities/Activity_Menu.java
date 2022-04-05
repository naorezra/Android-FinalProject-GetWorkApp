package com.shiranaor.GetWork.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.shiranaor.GetWork.Adapters.Adapter_Employee;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.shiranaor.GetWork.Activities.Activity_MyInquries.IS_CLIENT_EXTRA;

// This is the main page of the program which help the user navigate the app
public class Activity_Menu extends AppCompatActivity {

    private Menu myMenu;
    private AutoCompleteTextView searchEmployeeByTitle;
    private AutoCompleteTextView searchEmployeeBySubject;
    private AutoCompleteTextView searchEmployeeByLocation;
    private CheckBox searchByRating;
    private Button clientSignIn;

    ListView listView;
    Adapter_Employee adapter_employee;
    private Button menu_Button_employee;

    ArrayList<String> names = new ArrayList<>();
    boolean isUser = false;
    Button menu_Button_publishInquiries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViews();
        //Receive information from Firebase
        FireBase_RealTime.readAllBusinessNames(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_Menu.this,
                        android.R.layout.simple_spinner_item, ((ArrayList<String>) data));
                searchEmployeeByTitle.setAdapter(adapter);
            }
            @Override
            public void failed(String message) {
            }
        });

        FireBase_RealTime.readCities(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_Menu.this,
                        android.R.layout.simple_spinner_item,
                        ((ArrayList<String>) data));
                searchEmployeeByLocation.setAdapter(adapter);
            }

            @Override
            public void failed(String message) {

            }
        });

        FireBase_RealTime.readSubjects(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_Menu.this,
                        android.R.layout.simple_spinner_item, ((ArrayList<String>) data));
                searchEmployeeBySubject.setAdapter(adapter);
            }
            @Override
            public void failed(String message) {
            }
        });

        findViewById(R.id.menu_Button_publishInquiries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Menu.this, Activity_Inquiries.class);
                startActivity(intent);
            }
        });
        clientSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FireBase_Auth.isLogedIn()) {
                    Toast.makeText(Activity_Menu.this, "הנך מחובר", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Activity_Menu.this, Activity_SignIn_Client.class);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.menu_Button_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Menu.this, Activity_SignIn_Employee.class);
                startActivity(intent);
            }
        });
        listView = findViewById(R.id.listView);
    }

    private void findViews() {
        menu_Button_publishInquiries = findViewById(R.id.menu_Button_publishInquiries);
        menu_Button_employee = findViewById(R.id.menu_Button_employee);
        searchEmployeeBySubject = findViewById(R.id.menu_EditText_searchEmployeeBySubject);
        searchEmployeeByLocation = findViewById(R.id.menu_EditText_searchEmployeeByLocation);
        searchByRating = findViewById(R.id.checkBox_searchByRating);
        searchEmployeeByTitle = findViewById(R.id.searchEmployeeByTitle);
        clientSignIn = findViewById(R.id.menu_Button_client);
    }


    // this function handles the search of employees
    public void search(View view) {
        String letter = searchEmployeeByTitle.getText().toString();
        FireBase_RealTime.readAllEmployees(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Employee> employeeArrayList = (ArrayList<Employee>) data;
                employeeArrayList = filterEmployee(employeeArrayList);
                // if need to sort by rating
                if (searchByRating.isChecked()) {
                    // sorts the array by each employee rating (total rating / amount of raters)
                    employeeArrayList.sort(Comparator.comparingInt(employee -> {
                        if (employee.getAmountOfRaters() == 0) {
                            return 0;
                        }
                        return employee.getRating() / employee.getAmountOfRaters();
                    }));
                }
                // Reverse the array to get the highest start first
                Collections.reverse(employeeArrayList);
                adapter_employee = new Adapter_Employee(employeeArrayList, Activity_Menu.this, "");
                listView.setAdapter(adapter_employee);
            }

            @Override
            public void failed(String message) {

            }
        });

    }

    //Search for a professional by filters
    private ArrayList<Employee> filterEmployee(ArrayList<Employee> employeeArrayList) {
        ArrayList<Employee> result = new ArrayList<>();
        String businessName = searchEmployeeByTitle.getText().toString().trim();
        String subject = searchEmployeeBySubject.getText().toString().trim();
        String location = searchEmployeeByLocation.getText().toString().trim();
        for (Employee e : employeeArrayList) {
            if (!businessName.isEmpty()) {
                if (!e.getBusinessName().startsWith(businessName)) {
                    continue;
                }
            }
            if (!subject.isEmpty()) {
                if (!e.getSubject().startsWith(subject)) {
                    continue;
                }
            }
            if (!location.isEmpty()) {
                if (!e.getLocation().startsWith(location)) {
                    continue;
                }
            }
            result.add(e);
        }
        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkUserLogedIn();
    }
    // Check if a user is logged in and view a custom page
    private void checkUserLogedIn() {
        if (!FireBase_Auth.isLogedIn()) {
            menu_Button_publishInquiries.setVisibility(View.GONE);
            menu_Button_employee.setVisibility(View.VISIBLE);
            if (myMenu != null) {
                myMenu.findItem(R.id.myMenu_logout).setVisible(false);
                myMenu.findItem(R.id.myMenu_editDetails).setVisible(false);
                myMenu.findItem(R.id.myMenu_massage).setVisible(false);
                myMenu.findItem(R.id.myMenu_myInquiries).setVisible(false);
            }
        } else {
            menu_Button_publishInquiries.setVisibility(View.VISIBLE);
            menu_Button_employee.setVisibility(View.GONE);
            if (myMenu != null) {
                myMenu.findItem(R.id.myMenu_logout).setVisible(true);
                myMenu.findItem(R.id.myMenu_editDetails).setVisible(true);
                myMenu.findItem(R.id.myMenu_massage).setVisible(true);
                myMenu.findItem(R.id.myMenu_myInquiries).setVisible(true);
            }
        }

    }


    // help the upper menu on right corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        myMenu = menu;
        checkUserLogedIn();
        return true;
    }

    @Override
    // Select actions from options
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.myMenu_logout:
                logout();
                return true;
            case R.id.myMenu_myInquiries:
                openMyInquiries();
                return true;
            case R.id.myMenu_editDetails:
                openUpdateClient();
                return true;
            case R.id.myMenu_AboutPage:
                openAboutPage();
                return true;
            case R.id.myMenu_massage:
                openChats();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //open About Page activity
    private void openAboutPage() {
        Intent intent = new Intent(this, Activity_AboutPage.class);
        startActivity(intent);
    }
    //open  client Chats page
    private void openChats(){
        Intent intent = new Intent(Activity_Menu.this, Activity_Conversations.class);
        intent.putExtra(IS_CLIENT_EXTRA, true);
        startActivity(intent);
    }
    //open Update Client details page
    private void openUpdateClient() {
        Intent intent = new Intent(this, Activity_ClientUpdateDetails.class);
        startActivity(intent);
    }
    //open client inquiries page
    private void openMyInquiries() {
        Intent intent = new Intent(this, Activity_MyInquries.class);
        intent.putExtra(IS_CLIENT_EXTRA, true);
        startActivity(intent);
    }

    //Disengagement function
    private void logout() {
        FireBase_Auth.logout();
        Toast.makeText(Activity_Menu.this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
        checkUserLogedIn();
        Intent intent = new Intent(Activity_Menu.this, Activity_Menu.class);
        startActivity(intent);
    }

}