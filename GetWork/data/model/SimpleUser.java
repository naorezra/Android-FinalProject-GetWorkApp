package com.shiranaor.GetWork.data.model;

public abstract class  SimpleUser {
   protected String firstName;
   protected String lastName;
   protected String location;
   protected String image;
   protected String phoneNumber;
   protected String email;
   protected String id;
    //constructor
    public SimpleUser(String firstName, String lastName, String location, String image, String phoneNumber, String email, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.image = image;

        this.phoneNumber = phoneNumber;
        this.email = email;
        this.id = id;
    }
    //empty constructor
    public SimpleUser(){ }
    //getters&setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    //func
    public boolean hasProfilePicture() {
        return this.image!= null && this.image.length() > 0;
    }

}
