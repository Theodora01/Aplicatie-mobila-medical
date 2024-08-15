package com.example.aplicatiemedicala;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultatieFizicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultatieFizicFragment extends Fragment {

    private Spinner spSpecializare;
    private DatabaseReference reference;
    private List<String> allSpecializari = new ArrayList<>();
    private DoctorAdapter adapter;
    private List<Doctor> listDoctor = new ArrayList<>();
    private RecyclerView rvConsultatieFizic;
    private CoordinatorLayout coordinatorLayout;
    private FrameLayout frameLayoutConsultatieDoctor;
    private LinearLayout linearLayoutAppointmentFizic;
    public ConsultatieFizicFragment() {
    }

    public static ConsultatieFizicFragment newInstance(String param1, String param2) {
        ConsultatieFizicFragment fragment = new ConsultatieFizicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_consultatie_fizic, container, false);

        linearLayoutAppointmentFizic = view.findViewById(R.id.linearLayoutAppointemntFizic);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorMain);
        spSpecializare = view.findViewById(R.id.spSpecializareFizic);
        rvConsultatieFizic = view.findViewById(R.id.rvConsultatieFizic);
        frameLayoutConsultatieDoctor = view.findViewById(R.id.frameLayoutConsultatieDoctor);

        allSpecializari.add("Cardiology");
        allSpecializari.add("Pulmonology");
        allSpecializari.add("Pediatrics");
        allSpecializari.add("Neurology");
        allSpecializari.add("Psychiatry");
        allSpecializari.add("Orthopaedics");
        allSpecializari.add("Gynaecology");
        allSpecializari.add("Internal Medicine");
        allSpecializari.add("ENT");
        allSpecializari.add("Ophthalmology");
        allSpecializari.add("Anaesthesia");
        allSpecializari.add("General Surgery");
        allSpecializari.add("Infectious diseases");

        ArrayAdapter<String> adapterSpecializari = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, allSpecializari) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        adapterSpecializari.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spSpecializare.setAdapter(adapterSpecializari);

        spSpecializare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                System.out.println(item);

                reference = FirebaseDatabase.getInstance().getReference("doctors");
                reference.orderByChild("specializare").equalTo(item).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        listDoctor.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Doctor doctor = dataSnapshot.getValue(Doctor.class);

                            listDoctor.add(doctor);
                        }
                        try {
                            adapter = new DoctorAdapter(listDoctor, getContext(), new DoctorAdapter.OnDoctorClickListener() {
                                @Override
                                public void onDoctorClick(Doctor doctor) {
                                    FragmentConsultatieFizicDoctor profileDoctorFragment = FragmentConsultatieFizicDoctor.newInstance(doctor);
                                    coordinatorLayout.setVisibility(View.VISIBLE);

                                    if (profileDoctorFragment != null) {
                                        linearLayoutAppointmentFizic.setVisibility(View.GONE);
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.frameLayoutConsultatieDoctor, profileDoctorFragment);

                                        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        Log.d("Fragment", "deschis " + doctor.getNume());
                                    } else {
                                        Log.d("Fragment", "nu poate fi deschis ");
                                    }
                                }
                            });
                        }catch (InflateException e){
                            Log.e("ProfileDoctorFragment", "Error inflating the layout: " + e.getMessage(), e);
                        }
                        rvConsultatieFizic.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvConsultatieFizic.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }
}