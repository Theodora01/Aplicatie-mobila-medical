package com.example.aplicatiemedicala;

public class Articol {
    private String doctorName;
    private String specialization;
    private String articleLink;
    private String imageUrl;

    public Articol() {}

    public Articol(String doctorName, String specialization, String articleLink, String imageUrl) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.articleLink = articleLink;
        this.imageUrl = imageUrl;
    }
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
