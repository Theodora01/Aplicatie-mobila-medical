package com.example.aplicatiemedicala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText nameRegister, mailRegister, phoneRegister, dateBirth, passwordRegister;
    RadioGroup genderGroup;
    RadioButton genderButton;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameRegister = findViewById(R.id.nameRegister);
        mailRegister = findViewById(R.id.mailRegister);
        phoneRegister = findViewById(R.id.mobileRegister);
        dateBirth = findViewById(R.id.dateOfBirth);
        passwordRegister = findViewById(R.id.passwordRegister);
        registerBtn = findViewById(R.id.btnRegister);

        genderGroup = findViewById(R.id.radioGroupGender);
        genderGroup.clearCheck();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = genderGroup.getCheckedRadioButtonId();
                genderButton = findViewById(selectedGenderId);

                String name=nameRegister.getText().toString();
                String mail=mailRegister.getText().toString();
                String phone=phoneRegister.getText().toString();
                String birthday= dateBirth.getText().toString();
                String textGender;
                String password=passwordRegister.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    nameRegister.setError("Full Name is required");
                    nameRegister.requestFocus();
                }else if(TextUtils.isEmpty(mail)){
                    Toast.makeText(RegisterActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    mailRegister.setError("Email is required");
                    mailRegister.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    mailRegister.setError("Valid email is required");
                    mailRegister.requestFocus();
                }else if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
                    phoneRegister.setError("Phone number is required");
                    phoneRegister.requestFocus();
                }else if(phone.length()!=10){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    phoneRegister.setError("Phone number is required");
                    phoneRegister.requestFocus();
                } else if(TextUtils.isEmpty(birthday)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    dateBirth.setError("Date of Birth is required");
                    dateBirth.requestFocus();
                }else if(genderGroup.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    nameRegister.setError("Gender is required");
                    nameRegister.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    passwordRegister.setError("Password is required");
                    passwordRegister.requestFocus();
                } else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    passwordRegister.setError("Password too weak");
                    passwordRegister.requestFocus();
                }else{
                    textGender=genderButton.getText().toString();

                    registerUser(name,mail,phone,birthday,textGender,password);
                }
            }
        });

    }
    private void registerUser(String name, String mail, String phone, String birthday, String textGender, String password) {
        FirebaseAuth auth=FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser= auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_LONG).show();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        reference.setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                            List<String> favoriteDoctors= new ArrayList<>();
                            HelperClass user = new HelperClass(userId,name, mail, phone, birthday, textGender,password,favoriteDoctors);
                            user.setFavoriteDoctors(favoriteDoctors);
                            reference.setValue(user);

                            firebaseUser.sendEmailVerification();

                            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Toast.makeText(RegisterActivity.this, "User registered failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}