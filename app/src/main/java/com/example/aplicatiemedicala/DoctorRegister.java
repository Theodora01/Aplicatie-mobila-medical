package com.example.aplicatiemedicala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class DoctorRegister extends AppCompatActivity {

    EditText nameRegisterDoctor, specializareRegisterDoctor, mobileRegisterDoctor, steleRegisterDoctor,orarLucruRegisterDoctor,tarifConsultatieRegisterDoctor,tarifControlRegisterDoctor,experientaRegisterDoctor;
    RadioGroup radioGroupGenderMedic;
    RadioButton genderButton;
    Button registerButtonDoctor;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        nameRegisterDoctor = findViewById(R.id.nameRegisterDoctor);
        specializareRegisterDoctor=findViewById(R.id.specializareRegisterDoctor);
        mobileRegisterDoctor = findViewById(R.id.mobileRegisterDoctor);
        steleRegisterDoctor = findViewById(R.id.steleRegisterDoctor);
        orarLucruRegisterDoctor=findViewById(R.id.orarLucruRegisterDoctor);
        tarifConsultatieRegisterDoctor=findViewById(R.id.tarfifConsultatieRegisterDoctor);
        tarifControlRegisterDoctor=findViewById(R.id.tarifControlLucruRegisterDoctor);
        experientaRegisterDoctor=findViewById(R.id.experientaRegisterDoctor);
        registerButtonDoctor = findViewById(R.id.btnRegisterDoctor);

        radioGroupGenderMedic = findViewById(R.id.radioGroupGenderMedic);
        radioGroupGenderMedic.clearCheck();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        registerButtonDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupGenderMedic.getCheckedRadioButtonId();
                genderButton = findViewById(selectedGenderId);

                String nume=nameRegisterDoctor.getText().toString();
                String specializare=specializareRegisterDoctor.getText().toString();
                String orarLucru=orarLucruRegisterDoctor.getText().toString();
                Double tarifConsultatie= Double.valueOf(tarifConsultatieRegisterDoctor.getText().toString());
                Double tarifControl= Double.valueOf(tarifControlRegisterDoctor.getText().toString());
                int experienta= Integer.parseInt(experientaRegisterDoctor.getText().toString());
                Double stele= Double.valueOf(steleRegisterDoctor.getText().toString());
                String textGender;
                textGender=genderButton.getText().toString();
                String telefon=mobileRegisterDoctor.getText().toString();


                registerDoctor(nume,specializare,stele,textGender,telefon,tarifConsultatie,tarifControl,experienta,orarLucru);
            }
        });
    }

    private void registerDoctor(String nume,String specializare,Double stele,String gen,String telefon,Double tarifConsultatie,Double tarifControl,int experienta,String orarLucru){
        String doctorId = mDatabase.child("doctors").push().getKey();
        Doctor doctor = new Doctor(doctorId,nume,specializare,stele,gen,telefon,orarLucru,tarifConsultatie,tarifControl,experienta);

        mDatabase.child("doctors").child(doctorId).setValue(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DoctorRegister.this, "Doctorul a fost înregistrat cu succes!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DoctorRegister.this, "Eroare înregistrare doctor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}