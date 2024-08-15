package com.example.aplicatiemedicala;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment implements Parcelable {

    private String idProgramare;
    private String idDoctor;
    private String idPacient;
    private String ziua;
    private String data;
    private String ora;
    private String luna;
    private String tipProgramare;
    private String numeMedic;
    private Double pret;
    private String specializare;

    public Appointment() {
    }

    public Appointment(String idProgramare,String idDoctor, String idPacient, String ziua, String data, String ora, String luna, String tipProgramare, String numeMedic, Double pret,String specializare) {
        this.idProgramare= idProgramare;
        this.idDoctor = idDoctor;
        this.idPacient = idPacient;
        this.ziua = ziua;
        this.data = data;
        this.ora = ora;
        this.luna = luna;
        this.tipProgramare = tipProgramare;
        this.numeMedic = numeMedic;
        this.pret = pret;
        this.specializare = specializare;
    }

    protected Appointment(Parcel in) {
        idProgramare = in.readString();
        idDoctor = in.readString();
        idPacient = in.readString();
        ziua = in.readString();
        data = in.readString();
        ora = in.readString();
        luna = in.readString();
        tipProgramare = in.readString();
        numeMedic = in.readString();
        if (in.readByte() == 0) {
            pret = null;
        } else {
            pret = in.readDouble();
        }
        specializare = in.readString();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getSpecializare() {
        return specializare;
    }

    public void setSpecializare(String specializare) {
        this.specializare = specializare;
    }

    public String getIdProgramare() {
        return idProgramare;
    }

    public void setIdProgramare(String idProgramare) {
        this.idProgramare = idProgramare;
    }

    public String getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getIdPacient() {
        return idPacient;
    }

    public void setIdPacient(String idPacient) {
        this.idPacient = idPacient;
    }

    public String getZiua() {
        return ziua;
    }

    public void setZiua(String ziua) {
        this.ziua = ziua;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getLuna() {
        return luna;
    }

    public void setLuna(String luna) {
        this.luna = luna;
    }

    public String getTipProgramare() {
        return tipProgramare;
    }

    public void setTipProgramare(String tipProgramare) {
        this.tipProgramare = tipProgramare;
    }

    public String getNumeMedic() {
        return numeMedic;
    }

    public void setNumeMedic(String numeMedic) {
        this.numeMedic = numeMedic;
    }

    public Double getPret() {
        return pret;
    }

    public void setPret(Double pret) {
        this.pret = pret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idProgramare);
        dest.writeString(idDoctor);
        dest.writeString(idPacient);
        dest.writeString(ziua);
        dest.writeString(data);
        dest.writeString(ora);
        dest.writeString(luna);
        dest.writeString(tipProgramare);
        dest.writeString(numeMedic);
        if (pret == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pret);
        }
        dest.writeString(specializare);
    }
    public long getTimeInMillis() {
        String dateString = data + " " +ziua +" " + luna+ " "+ ora+ " 2024";
        SimpleDateFormat sdf = new SimpleDateFormat("d EEE MMM HH:mm yyyy", Locale.ENGLISH);

        try {
            Date date = sdf.parse(dateString);

            if (date != null) {
                return date.getTime();
            } else {
                System.err.println("Failed to parse the date string.");
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
