package com.example.aplicatiemedicala;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class StartPage extends AppCompatActivity {

    Button startBtn, addArticole;
    TextView tvStart,tvSuntDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        startBtn=findViewById(R.id.btnStart);
        tvStart=findViewById(R.id.tvStart);
        tvSuntDoctor=findViewById(R.id.tvSuntDoctor);
        addArticole = findViewById(R.id.addArticole);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartPage.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartPage.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        tvSuntDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartPage.this, DoctorRegister.class);
                startActivity(intent);
            }
        });

        tvSuntDoctor.setVisibility(View.GONE);
        addArticole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, Articole.class);
                startActivity(intent);
            }
        });
        addArticole.setVisibility(View.GONE);

    }
}
