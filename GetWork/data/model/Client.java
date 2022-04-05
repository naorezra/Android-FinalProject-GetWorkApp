package com.shiranaor.GetWork.data.model;

public class Client extends SimpleUser {
    //constructor
    public Client(String firstName, String lastName, String location, String image, String phoneNumber, String email, String id) {
        super(firstName, lastName, location, image, phoneNumber, email, id);
    }
    //empty constructor
    public Client(){
        super();
    }
}
