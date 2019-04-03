package com.example.wurood.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;


public class Contact implements Serializable {

    private String name;
    private String contactNumber;
    private int favoritePhoto;
    private Bitmap photo;
    private boolean isFav;
    private Long id;
    private String email;


    public Contact(String name, String desc, Bitmap photo, Long id, int photofav, String email) {
        this.name = name;
        this.contactNumber = desc;
        this.photo = photo;
        this.id = id;
        this.favoritePhoto = photofav;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    public int getPhotoFav() {
        return favoritePhoto;
    }

    public void setPhotoFav(int favoritephoto) {
        this.favoritePhoto = favoritephoto;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public Long getid() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}