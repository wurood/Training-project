package com.example.wurood.myapplication;

import android.graphics.Bitmap;

public class Message {
    private String msg;
    private int position;
    private int favoriteImage;
    private String contactName;
    private String contactNumber;
    private String contactEmail;
    private Bitmap contactImage;
    private long contactId;
    public Message(int position, String contactName, String contactNumber, Bitmap contactImage, long contactId, int favoriteImage, String contactEmail) {
        this.msg = msg;
        this.position=position;
        this.contactName=contactName;
        this.contactNumber=contactNumber;
        this.contactImage=contactImage;
        this.contactId=contactId;
        this.favoriteImage=favoriteImage;
        this.contactEmail=contactEmail;
    }

    public String getNamea() {
        return contactName;
    }
    public String getNumbera() {
        return contactNumber;
    }
    public String getEmaila() {
        return contactEmail;
    }
    public long getIda() {
        return contactId;
    }
    public int getPositiona() {
        return position;
    }
    public int getFavoriteImagea() {
        return favoriteImage;
    }
    public Bitmap getImagea() {
        return contactImage;
    }



}
