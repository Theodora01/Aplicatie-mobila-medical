package com.example.aplicatiemedicala;

import java.util.ArrayList;
import java.util.List;

public class HelperClass {
    String id,name,email, mobile, dateBirth,gender, password;
    List<String> favoriteDoctors;
    public HelperClass() {
    }

    public HelperClass(String id,String name, String email, String mobile, String dateBirth, String gender, String password,List<String> favoriteDoctors) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dateBirth = dateBirth;
        this.gender = gender;
        this.password = password;
        this.id=id;
        this.favoriteDoctors=favoriteDoctors;
    }

    public List<String> getFavoriteDoctors() {
        return favoriteDoctors;
    }

    public void setFavoriteDoctors(List<String> favoriteDoctors) {
        this.favoriteDoctors = favoriteDoctors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
