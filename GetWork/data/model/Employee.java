package com.shiranaor.GetWork.data.model;

public class Employee extends SimpleUser {
    private String diplomaImage;
    private String businessName;
    private String uploadsId;
    private int rating;
    private String subject;
    private int amountOfRaters;
    //constructor
    public Employee(String firstName, String lastName, String subject, String location, String diplomaImage, String image, String phoneNumber, String email, String id, String businessName, String uploadsId) {
        super(firstName, lastName, location, image, phoneNumber, email, id);
        this.diplomaImage = diplomaImage;
        this.businessName = businessName;
        this.uploadsId = uploadsId;
        this.subject = subject;
        this.rating = 0;
        this.amountOfRaters = 0;
    }
    //empty constructor
    public Employee() {
        super();
    }
    //getters&setters
    public String getDiplomaImage() {
        return diplomaImage;
    }

    public void setDiplomaImage(String diplomaImage) {
        this.diplomaImage = diplomaImage;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getUploadsId() {
        return uploadsId;
    }

    public void setUploadsId(String uploadsId) {
        this.uploadsId = uploadsId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAmountOfRaters() {
        return amountOfRaters;
    }

    public void setAmountOfRaters(int amountOfRaters) {
        this.amountOfRaters = amountOfRaters;
    }
}
