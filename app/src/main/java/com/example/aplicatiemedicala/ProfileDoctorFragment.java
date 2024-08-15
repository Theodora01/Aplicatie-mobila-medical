package com.example.aplicatiemedicala;

import static com.example.aplicatiemedicala.R.id.btnBackProfileDoctor;
import static com.example.aplicatiemedicala.R.id.imageProfileDoctor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.zip.Inflater;

public class ProfileDoctorFragment extends Fragment  {

    private Button btnProfileDoctor;
    public static ProfileDoctorFragment newInstance(Doctor doctor) {
        ProfileDoctorFragment fragment = new ProfileDoctorFragment();
        Bundle args = new Bundle();
        args.putParcelable("doctor", doctor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile_doctor,container,false);

        btnProfileDoctor = view.findViewById(R.id.btnProfileDoctor);

        Bundle args = getArguments();
        if (args != null) {
            Doctor doctor = args.getParcelable("doctor");
            if (doctor != null) {
                TextView nameDoctor = view.findViewById(R.id.nameDoctor);
                nameDoctor.setText(doctor.getNume());

                TextView specializareDoctor = view.findViewById(R.id.specializareDoctor);
                specializareDoctor.setText(doctor.getSpecializare());

                TextView tvExperienta=view.findViewById(R.id.tvExperientaDoctor);
                tvExperienta.setText(String.valueOf(doctor.getExperienta()));

                TextView steleDoctor = view.findViewById(R.id.steleDoctor);
                steleDoctor.setText(Double.toString(doctor.getStele()));

                TextView tvPretConsultatie=view.findViewById(R.id.tvPretConsultatie);
                tvPretConsultatie.setText(Double.toString(doctor.getTarifConsultatie()));

                TextView tvPretControl=view.findViewById(R.id.tvPretControl);
                tvPretControl.setText(Double.toString(doctor.getTarifControl()));

                ImageView imageProfileDoctor=view.findViewById(R.id.imageProfileDoctor);
                if(doctor.getGen().equals("Female")){
                    imageProfileDoctor.setImageResource(R.drawable.femaleprofile);
                }else{
                    imageProfileDoctor.setImageResource(R.drawable.maleprofile);
                }

                ImageView imgBackProfileDoctor =view.findViewById(R.id.btnBackProfileDoctor);
                imgBackProfileDoctor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backToListDoctor(v);
                    }
                });
            }
        }
        btnProfileDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsultatieFizicFragment fragment= new ConsultatieFizicFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.frameLayoutPageConsultation, fragment);
                fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void backToListDoctor(View view) {
        FragmentManager fragmentManager= requireActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.frameLayoutProfileDoctor, new ListDoctorsFragment())
                .setCustomAnimations(R.anim.slide_out,R.anim.slide_in)
                .addToBackStack(null)
                .commit();
    }
}