package com.shiranaor.GetWork.MyFireBase;

import static com.shiranaor.GetWork.Activities.Activity_Employee.DIPLOMA_IMAGE;
import static com.shiranaor.GetWork.Activities.Activity_Employee.USER_IMAGE;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.data.model.ChatMessage;
import com.shiranaor.GetWork.data.model.Client;
import com.shiranaor.GetWork.data.model.Conversation;
import com.shiranaor.GetWork.data.model.Employee;
import com.shiranaor.GetWork.data.model.Inquirie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// this class is tha main connection of the project with the  to firebase DB and storage.
public class FireBase_RealTime {
    // firebase paths:
    private static final String USERS_CLIENT_PATH = "clients";
    public static final String INQUIRIES_PATH = "inquiries";
    public static final String CITIES_PATH = "cities";
    public static final String SUBJECTS_PATH = "subjects";
    private static final String EMPLOYEE_CLIENT_PATH = "employees";
    public static final String IMAGES = "images";
    public static final String CHATS = "chats";
    public static final String UPLOADS = "uploads";

    // firebase references
    private static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // instance for firebase storage and StorageReference
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageReference = storage.getReference();


    // this function will creat a new employee
    public static void createNewEmployee(Employee employee, Callback_Success callback_success) {
        String id = FireBase_Auth.getUserId();
        rootRef.child(EMPLOYEE_CLIENT_PATH).child(id).setValue(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadImage(employee.getDiplomaImage(), id, employee.getId(), EMPLOYEE_CLIENT_PATH, "diplomaImage");
                if (employee.hasProfilePicture()) {
                    uploadImage(employee.getImage(), id, employee.getId(), EMPLOYEE_CLIENT_PATH, "image");
                }
                callback_success.success("עובד נרשם בהצלחה");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function will creat a new client
    public static void createNewClient(Client client, Callback_Success callback_success) {
        String id = FireBase_Auth.getUserId();
        rootRef.child(USERS_CLIENT_PATH).child(FireBase_Auth.getUserId()).setValue(client).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (client.hasProfilePicture()) {
                    uploadImage(client.getImage(), id, client.getId(), USERS_CLIENT_PATH, "image");
                }
                callback_success.success("!נרשמת בהצלחה");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.success(e.getLocalizedMessage());
            }
        });
    }

    // this function will create a new inquiry
    public static void createNewInquirie(Inquirie inquirie, Callback_Success callback_success) {
        String key = rootRef.push().getKey();
        inquirie.setId(key);
        rootRef.child(INQUIRIES_PATH).child(key).setValue(inquirie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback_success.success("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.success(e.getLocalizedMessage());
            }
        });
    }

    // this function will return a inquiry by id
    public static void readInquirieById(String id, Callback_ReadData callback_readData) {
        rootRef
                .child(INQUIRIES_PATH).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback_readData.success(((Inquirie) dataSnapshot.getValue(Inquirie.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function will return a employee  by his id
    public static void readEmployeeById(String id, Callback_ReadData callback_readData) {
        rootRef
                .child(EMPLOYEE_CLIENT_PATH).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback_readData.success(((Employee) dataSnapshot.getValue(Employee.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function will return a specific client by his id
    public static void readClientById(String id, Callback_ReadData callback_readData) {
        rootRef
                .child(USERS_CLIENT_PATH).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback_readData.success(((Client) dataSnapshot.getValue(Client.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function will update client details
    public static void updateClient(Client client, Callback_Success callback_success) {
        rootRef
                .child(USERS_CLIENT_PATH).child(client.getId()).setValue(client).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (client.hasProfilePicture() && !client.getImage().contains("firebasestorage.googleapis.com")) {
                    uploadImage(client.getImage(), UUID.randomUUID().toString(), client.getId(), USERS_CLIENT_PATH, "image");
                }
                callback_success.success("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function will update employee details
    public static void updateEmployee(Employee employee, Callback_Success callback_success) {
        rootRef
                .child(EMPLOYEE_CLIENT_PATH).child(employee.getId()).setValue(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (employee.hasProfilePicture() && !employee.getImage().contains("firebasestorage.googleapis.com")) {
                    uploadImage(employee.getImage(), UUID.randomUUID().toString(), employee.getId(), EMPLOYEE_CLIENT_PATH, "image");
                }
                callback_success.success("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function will update inquiry
    public static void updateInquirie(Inquirie inquirie, Callback_Success callback_success) {
        rootRef
                .child(INQUIRIES_PATH).child(inquirie.getId()).setValue(inquirie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback_success.success("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function will delete an inquiry
    public static void deleteInquirie(String id, Callback_Success callback_success) {
        rootRef
                .child(INQUIRIES_PATH).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback_success.success("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function deltes all chat images and the chat itselfs
    public static void deleteChat(String id, Callback_Success callback_success) {
        // first of all, delete all of the images from this chat
        rootRef.child(CHATS).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    // get the url of the image from message
                    String cuurentImageUrl = (String) dataSnap.child("imgSrc").getValue();
                    if (cuurentImageUrl != null && cuurentImageUrl.length() > 0) {
                        deleteImage(cuurentImageUrl);
                    }
                    // Use your object as needed
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // after deleting all images, delete the chat
        rootRef
                .child(CHATS).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback_success.success("Deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback_success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this function will return a list of all business names
    public static void readAllBusinessNames(Callback_ReadData callback_readData) {
        readAllEmployees(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<String> names = new ArrayList<>();
                for (Employee employee : ((ArrayList<Employee>) data)) {
                    names.add(employee.getBusinessName());
                }
                callback_readData.success(names);
            }
            @Override
            public void failed(String message) {
                callback_readData.failed("Error getting data from server");
            }
        });
    }

    // this function will return all of the inquiries
    public static void readAllInquiries(Callback_ReadData callback_readData) {
        ArrayList<Inquirie> inquirieArrayList = new ArrayList<>();
        rootRef
                .child(INQUIRIES_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            inquirieArrayList.add(dataSnap.getValue(Inquirie.class));
                            // Use your object as needed
                        }
                        callback_readData.success(inquirieArrayList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }

                });
    }

    // this function returns all subjects of employees
    public static void readEmployeeSubject(Callback_ReadData callback_readData) {
        rootRef
                .child(EMPLOYEE_CLIENT_PATH).child(FireBase_Auth.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Employee employee = ((Employee) dataSnapshot.getValue(Employee.class));
                        callback_readData.success(employee.getSubject());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function let us know if current user is employee or not
    public static void isEmployee(Callback_ReadData callback_readData) {
        rootRef
                .child(EMPLOYEE_CLIENT_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            if (dataSnap.getKey().equals(FireBase_Auth.getUserId())) {
                                callback_readData.success(true);
                                return;
                            }
                        }
                        callback_readData.success(false);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function will return all of the employees
    public static void readAllEmployees(Callback_ReadData callback_readData) {
        ArrayList<Employee> employeeArrayList = new ArrayList<>();
        rootRef
                .child(EMPLOYEE_CLIENT_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            employeeArrayList.add(dataSnap.getValue(Employee.class));
                            // Use your object as needed
                        }
                        callback_readData.success(employeeArrayList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback_readData.failed(error.getMessage());
                    }
                });
    }

    // this function will return all employees by a list of id's
    public static void readEmployeesByIds(ArrayList<String> employessIds, Callback_ReadData callback_readData) {
        readAllEmployees(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Employee> employees = (ArrayList<Employee>) data;
                ArrayList<Employee> result = new ArrayList<>();
                for (Employee employee : employees) {
                    if (employessIds.contains(employee.getId())) {
                        result.add(employee);
                    }
                }
                callback_readData.success(result);
            }
            @Override
            public void failed(String message) {
            }
        });
    }

    // UploadImage method
    public static void uploadImage(String filePath, String id, String userId, String collectionName, String key) {
        if (filePath != null && filePath.length() > 0) {
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(IMAGES + "/" + id);
            // adding listeners on upload
            // or failure of image
            ref.putFile(Uri.parse(filePath))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map<String, Object> thingsToChange = new HashMap<>();
                                            thingsToChange.put(key, uri.toString());
                                            // Edit the image value to the final uri
                                            editSpecificFields(collectionName, userId, thingsToChange);
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("app", "error uploading image " + e.toString());
                        }
                    });
        }
    }


    // UploadImage method
    public static void uploadChatImage(String filePath, String id, Callback_Success success) {
        if (filePath != null && filePath.length() > 0) {
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(IMAGES + "/chats/" + id);
            // adding listeners on upload
            // or failure of image
            ref.putFile(Uri.parse(filePath)).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            success.success(uri.toString());
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("app", "error uploading image " + e.toString());
                        }
                    });
        }
    }

    // this function rates an employee
    public static void rateEmployee(Employee employee, int rate) {
        Map<String, Object> thingsToChange = new HashMap<>();
        thingsToChange.put("rating", employee.getRating() + rate);
        thingsToChange.put("amountOfRaters", employee.getAmountOfRaters() + 1);
        editSpecificFields(EMPLOYEE_CLIENT_PATH, employee.getId(), thingsToChange);
    }

    // This methos allows to edit a value by key on specific collection on the db
    public static void editSpecificFields(String collectionName, String childId, Map<String, Object> thingsToChange) {
        DatabaseReference additionalUserInfoRef = rootRef.child(collectionName);
        Query userQuery = additionalUserInfoRef.orderByChild("id").equalTo(childId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().updateChildren(thingsToChange);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Changing data", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    // this function will return all of the candidates to a specific inquiry
    public static void readInquiryCandidates(String inquiryId, Callback_ReadData callback_readData) {
        rootRef.child(INQUIRIES_PATH + "/" + inquiryId).child("candidates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> employees = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    employees.add(dataSnap.getValue(String.class));
                }
                callback_readData.success(employees);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // this function will read all cities for to dropdown
    public static void readCities(Callback_ReadData callback_readData) {
        rootRef.child(CITIES_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> cities = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    cities.add(dataSnap.getValue(String.class));
                }
                callback_readData.success(cities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // this function will read all subjects for to dropdown
    public static void readSubjects(Callback_ReadData callback_readData) {
        rootRef.child(SUBJECTS_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> subjects = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    subjects.add(dataSnap.getValue(String.class));
                }
                callback_readData.success(subjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // this function will return all conversations of user by id
    public static void readAllUserConversations(String userId, boolean isClient, Callback_ReadData callback_readData) {
        ArrayList<Conversation> conversations = new ArrayList<>();
        rootRef.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    // Extract conersationId out of firebase result
                    String conversationId = (dataSnap.getKey().toString()); // if user belongs to this chat
                    // if user belongs to this chat
                    if (conversationId.contains(userId)) {
                        conversations.add(new Conversation(conversationId));
                    }
                }
                fillConversationsWithData(conversations, userId, isClient, new Callback_Success() {
                    @Override
                    public void success(String message) {
                        callback_readData.success(conversations);
                    }
                    @Override
                    public void failed(String message) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback_readData.failed(error.getMessage());
            }
        });
    }


    // To help check if all data was changed
    public static int sucssesCounter;

    // Take array which in every conversation there is only id, no image or name and fill this values
    public static void fillConversationsWithData(@NonNull ArrayList<Conversation> conversations, String userId, boolean isClient, Callback_Success callback) {
        sucssesCounter = 0;
        for (Conversation conversation : conversations) {
            String currentId = conversation.getId();
            currentId = currentId.replace(userId, "");
            currentId = currentId.replace("_", "");
            if (!isClient) {
                readClientById(currentId, new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        Client client = (Client) data;
                        conversation.setImg(client.getImage());
                        conversation.setName(client.getFirstName() + " " +
                                client.getLastName() + " \n" + client.getLocation());
                        sucssesCounter++;
                        if (sucssesCounter == conversations.size()) {
                            callback.success("finished");
                        }
                    }
                    @Override
                    public void failed(String message) {
                    }
                });
            } else {
                readEmployeeById(currentId, new Callback_ReadData() {
                    @Override
                    public void success(Object data) {
                        Employee employee = (Employee) data;
                        conversation.setImg(employee.getImage());
                        conversation.setName(employee.getFirstName() + " " +
                                employee.getLastName() + " \n" +
                                employee.getLocation());
                        sucssesCounter++;
                        if (sucssesCounter == conversations.size()) {
                            callback.success("finished");
                        }
                    }
                    @Override
                    public void failed(String message) {
                    }
                });
            }
        }
    }

    public static int calculatePosition(ArrayList<String> cities, String city) {
        return cities.indexOf(city);
    }


    public static void delete(String path, String id, Callback_Success callback_success) {
        mAuth.getCurrentUser().delete().addOnSuccessListener((res) -> {
            rootRef
                    .child(path).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callback_success.success("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback_success.failed(e.getLocalizedMessage());
                }
            });
        });
    }

    // this function will delete spesific album (upload) by send to id an empty string, or a specific image from the album by sending the id.
    public static void deleteUpload(String uploadsId, String id, Callback_Success success) {
        rootRef
                .child(UPLOADS).child(uploadsId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                success.success("Image deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success.failed(e.getLocalizedMessage());
            }
        });
    }

    // this functions delete image from storage based on a url
    public static void deleteImage(String url) {
        StorageReference photoRef = storage.getReferenceFromUrl(url);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("DELETE IMAGE", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("DELETE IMAGE", "onFailure: did not delete file");
            }
        });

    }

    // this function updates a specific image on employee by image type
    public static void updateEmployeeImage(int imageType, Employee employee, String newImageUri) {
        String imageId = UUID.randomUUID().toString();
        if (imageType == DIPLOMA_IMAGE) {
            uploadImage(newImageUri, imageId, employee.getId(), EMPLOYEE_CLIENT_PATH, "diplomaImage");
            deleteImage(employee.getDiplomaImage());
        } else if (imageType == USER_IMAGE) {
            if (employee.hasProfilePicture()) {
                deleteImage(employee.getImage());
            }
            uploadImage(newImageUri, imageId, employee.getId(), EMPLOYEE_CLIENT_PATH, "image");
        }
    }


    // this function delete employee and all of his images
    public static void deleteEmployee(Employee employee, Callback_Success callback_success) {
        deleteAllUserChats(employee.getId(), false);
        // delete photos of user
        deleteImage(employee.getDiplomaImage());
        if (employee.hasProfilePicture()) {
            deleteImage(employee.getImage());
        }
        // if employee has a album
        if (employee.getUploadsId() != null && employee.getUploadsId().length() > 0) {
            deleteUpload(employee.getUploadsId(), "", new Callback_Success() {
                @Override
                public void success(String message) {
                    Log.i("DELETE UPLOAD", "employee uploads deleted");
                }
                @Override
                public void failed(String message) {
                }
            });
        }
        delete(EMPLOYEE_CLIENT_PATH, employee.getId(), callback_success);
    }


    // this function deletes all client inquiries
    public static void deleteClientInquiries(String clientId) {
        readAllInquiries(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Inquirie> inquiries = (ArrayList<Inquirie>) data;
                // finding every inquiry of client and than delete them
                for (Inquirie currentInquirie : inquiries) {
                    // if this is a inquiry of this client
                    if (currentInquirie.getUserId().equals(clientId)) {
                        deleteInquirie(currentInquirie.getId(), new Callback_Success() {
                            @Override
                            public void success(String message) {
                                Log.i("DELETE INQUIRY", "inquiry deleted");
                            }
                            @Override
                            public void failed(String message) {
                            }
                        });
                    }
                }
            }
            @Override
            public void failed(String message) {
            }
        });
    }

    // this function delete client and all of his images
    public static void deleteClient(Client client, Callback_Success callback_success) {
        deleteAllUserChats(client.getId(), true);
        if (client.hasProfilePicture()) {
            deleteImage(client.getImage());
        }
        deleteClientInquiries(client.getId());
        delete(USERS_CLIENT_PATH, client.getId(), callback_success);
        callback_success.success("Client deleted!");
    }


    // this function searches all user chats and delete them
    public static void deleteAllUserChats(String id, boolean isClient) {
        readAllUserConversations(id, isClient, new Callback_ReadData() {
            @Override
            public void success(Object data) {
                ArrayList<Conversation> conversations = (ArrayList<Conversation>) data;
                // moving all over user chats to delete them
                for (Conversation conversation : conversations) {
                    deleteChat(conversation.getId(), new Callback_Success() {
                        @Override
                        public void success(String message) {
                            Log.i("DELETE CONVERSATION", "conversation deleted");
                        }
                        @Override
                        public void failed(String message) {
                        }
                    });
                }
            }
            @Override
            public void failed(String message) {
            }
        });
    }

}
