package com.example.aplicatiemedicala;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Doctor implements Parcelable {
    private String id;
    private String nume;
    private String specializare;
    private double stele;
    private String gen;
    private String telefon;
    private String orarLucru;
    private double tarifConsultatie;
    private double tarifControl;
    private int experienta;
    public Doctor() {
    }
    public Doctor(String id, String nume, String specializare, double stele, String gen, String telefon, String orarLucru, double tarifConsultatie, double tarifControl, int experienta) {
        this.id = id;
        this.nume = nume;
        this.specializare = specializare;
        this.stele = stele;
        this.gen = gen;
        this.telefon = telefon;
        this.orarLucru = orarLucru;
        this.tarifConsultatie = tarifConsultatie;
        this.tarifControl = tarifControl;
        this.experienta = experienta;
    }
    public Doctor(String nume, String specializare, double stele, String gen, String telefon) {
        this.nume = nume;
        this.specializare = specializare;
        this.stele = stele;
        this.gen = gen;
        this.telefon = telefon;
    }

    protected Doctor(Parcel in) {
        id = in.readString();
        nume = in.readString();
        specializare = in.readString();
        stele = in.readDouble();
        gen = in.readString();
        telefon = in.readString();
        orarLucru = in.readString();
        tarifConsultatie = in.readDouble();
        tarifControl = in.readDouble();
        experienta = in.readInt();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public String getNume() {
        return nume;
    }

    public String getGen() {return gen;}

    public double getStele() {return stele;}
    public String getSpecializare() {
        return specializare;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrarLucru() {
        return orarLucru;
    }

    public void setOrarLucru(String orarLucru) {
        this.orarLucru = orarLucru;
    }

    public double getTarifConsultatie() {
        return tarifConsultatie;
    }

    public void setTarifConsultatie(double tarifConsultatie) {
        this.tarifConsultatie = tarifConsultatie;
    }

    public double getTarifControl() {
        return tarifControl;
    }

    public void setTarifControl(double tarifControl) {
        this.tarifControl = tarifControl;
    }

    public int getExperienta() {
        return experienta;
    }

    public void setExperienta(int experienta) {
        this.experienta = experienta;
    }

    public String getTelefon() {return telefon;}

    public void setTelefon(String telefon) {this.telefon = telefon;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nume);
        dest.writeString(specializare);
        dest.writeDouble(stele);
        dest.writeString(gen);
        dest.writeString(telefon);
        dest.writeString(orarLucru);
        dest.writeDouble(tarifConsultatie);
        dest.writeDouble(tarifControl);
        dest.writeInt(experienta);
    }

    public void setName(String name) {
        this.nume=name;
    }

    public void setSpecializare(String specializare) {
        this.specializare=specializare;
    }

    public void setStele(Double stele) {
        this.stele=stele;
    }


}
