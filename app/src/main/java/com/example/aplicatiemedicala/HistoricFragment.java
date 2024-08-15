package com.example.aplicatiemedicala;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoricFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoricFragment extends Fragment {

    private RecyclerView rvPendingAppointment, rvCompletedAppointment,rvPendingAppointmentFizic,rvCompletedAppointmentFizic;
    private TextView tvPending, tvCompleted;
    private DatabaseReference referenceAppointment, referenceAppointmentFizic;
    private LinearLayout linearLayoutNoConsultation;
    private String idPacient;
    private HistoricAdapter pendingAdapter;
    private HistoricAdapter completedAdapter;
    private HistoricAdapterFizic pendingAdapterFizic;
    private HistoricAdapterFizic completedAdapterFizic;
    private List<Appointment> listAppointmentPending = new ArrayList<>();
    private List<AppointmentFizic> listAppointmentPendingFizic = new ArrayList<>();
    private List<Appointment> listAppointmentCompleted = new ArrayList<>();
    private List<AppointmentFizic> listAppointmentCompletedFizic = new ArrayList<>();

    public HistoricFragment() {
    }

    public static HistoricFragment newInstance(String param1, String param2) {
        HistoricFragment fragment = new HistoricFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_historic, container, false);

        rvPendingAppointment = view.findViewById(R.id.rvPendingAppointment);
        rvCompletedAppointment = view.findViewById(R.id.rvCompletedApointment);
        rvCompletedAppointmentFizic = view.findViewById(R.id.rvCompletedApointmentFizic);
        rvPendingAppointmentFizic = view.findViewById(R.id.rvPendingAppointmentFizic);

        tvCompleted = view.findViewById(R.id.tvCompleted);
        tvPending = view.findViewById(R.id.tvPending);

        linearLayoutNoConsultation = view.findViewById(R.id.linearLayoutNoConsultation);
        linearLayoutNoConsultation.setVisibility(View.GONE);

        String[] numeLuni = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Calendar currentDate = Calendar.getInstance();

        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        System.out.println("Ziua curenta "+currentDay);
        System.out.println("Luna curenta "+currentMonth);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        idPacient = firebaseUser.getUid();
        referenceAppointment = FirebaseDatabase.getInstance().getReference().child("appointment");
        referenceAppointmentFizic = FirebaseDatabase.getInstance().getReference().child("appointmentFizic");

        pendingAdapter =new HistoricAdapter(listAppointmentPending, getContext());
        rvPendingAppointment.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingAppointment.setAdapter(pendingAdapter);

        completedAdapter =new HistoricAdapter(listAppointmentCompleted, getContext());
        rvCompletedAppointment.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompletedAppointment.setAdapter(completedAdapter);

        pendingAdapterFizic =new HistoricAdapterFizic(listAppointmentPendingFizic,getContext());
        rvPendingAppointmentFizic.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingAppointmentFizic.setAdapter(pendingAdapterFizic);

        completedAdapterFizic =new HistoricAdapterFizic(listAppointmentCompletedFizic,getContext());
        rvCompletedAppointmentFizic.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompletedAppointmentFizic.setAdapter(completedAdapterFizic);


        referenceAppointment.orderByChild("idPacient").equalTo(idPacient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Appointment appointment = dataSnapshot.getValue(Appointment.class);

                        if (appointment != null) {
                            String appointmentMonth = appointment.getLuna();
                            String appointmentDate = appointment.getData();

                            int ziuaInt = Integer.parseInt(appointmentDate);

                            int lunaInt=-1;
                            for (int i = 0; i < numeLuni.length; i++) {
                                if (numeLuni[i].equals(appointmentMonth)) {
                                    lunaInt = i+1;
                                    break;
                                }
                            }

                            Log.d("DHistoric Fragment", "Month: " + lunaInt + "ziua" + ziuaInt);
                            Log.d("Data actuala", "Month"+currentMonth + "Day"+currentDay);
                            if (currentMonth == lunaInt) {
                                if (currentDay == ziuaInt) {
                                    listAppointmentCompleted.add(appointment);
                                } else if (currentDay > ziuaInt) {
                                    listAppointmentCompleted.add(appointment);
                                } else if (currentDay < ziuaInt){
                                    listAppointmentPending.add(appointment);
                                }
                            } else if (currentMonth < lunaInt) {
                                listAppointmentPending.add(appointment);
                            } else if (currentMonth > lunaInt){
                                listAppointmentCompleted.add(appointment);
                            }
                        }
                    }

                    pendingAdapter.notifyDataSetChanged();
                    completedAdapter.notifyDataSetChanged();

                    if(listAppointmentPending.size() > 0){
                        rvPendingAppointment.setVisibility(View.VISIBLE);
                        rvCompletedAppointment.setVisibility(View.GONE);
                        linearLayoutNoConsultation.setVisibility(View.GONE);
                    }
                    else{
                        rvPendingAppointment.setVisibility(View.GONE);
                        rvCompletedAppointment.setVisibility(View.GONE);
                        linearLayoutNoConsultation.setVisibility(View.VISIBLE);
                    }


                }
                pendingAdapter.notifyDataSetChanged();
                completedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceAppointmentFizic.orderByChild("idPacient").equalTo(idPacient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AppointmentFizic appointment = dataSnapshot.getValue(AppointmentFizic.class);

                        if (appointment != null) {
                            String appointmentMonth = appointment.getLuna();
                            String appointmentDate = appointment.getData();

                            int ziuaInt = Integer.parseInt(appointmentDate);


                            int lunaInt=-1;
                            for (int i = 0; i < numeLuni.length; i++) {
                                if (numeLuni[i].equals(appointmentMonth)) {
                                    lunaInt = i+1;
                                    break;
                                }
                            }


                            if (currentMonth == lunaInt) {
                                if (currentDay == ziuaInt) {
                                    listAppointmentCompletedFizic.add(appointment);
                                } else if (currentDay > ziuaInt) {
                                    listAppointmentCompletedFizic.add(appointment);
                                } else if (currentDay < ziuaInt){
                                    listAppointmentPendingFizic.add(appointment);
                                }
                            } else if (currentMonth < lunaInt) {
                                listAppointmentPendingFizic.add(appointment);
                            } else if (currentMonth > lunaInt){
                                listAppointmentCompletedFizic.add(appointment);
                            }
                        }
                    }

                    pendingAdapterFizic.notifyDataSetChanged();
                    completedAdapterFizic.notifyDataSetChanged();

                    if(listAppointmentPendingFizic.size() > 0){
                        rvPendingAppointmentFizic.setVisibility(View.VISIBLE);
                        rvCompletedAppointmentFizic.setVisibility(View.GONE);
                        linearLayoutNoConsultation.setVisibility(View.GONE);
                    }else{
                        rvPendingAppointmentFizic.setVisibility(View.GONE);
                        rvCompletedAppointmentFizic.setVisibility(View.GONE);
                        linearLayoutNoConsultation.setVisibility(View.VISIBLE);
                    }

                    pendingAdapterFizic.notifyDataSetChanged();
                    completedAdapterFizic.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(listAppointmentPendingFizic.size() <= 0 && listAppointmentPending.size() <= 0){
            linearLayoutNoConsultation.setVisibility(View.VISIBLE);
            rvPendingAppointment.setVisibility(View.GONE);
            rvCompletedAppointment.setVisibility(View.GONE);
            rvPendingAppointmentFizic.setVisibility(View.GONE);
            rvCompletedAppointmentFizic.setVisibility(View.GONE);
        }
        tvPending.setBackgroundResource(R.drawable.pressed_background);
        tvCompleted.setBackgroundResource(R.drawable.normal_background);

        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);
                v.startAnimation(animation);
                tvPending.setBackgroundResource(R.drawable.pressed_background);
                tvCompleted.setBackgroundResource(R.drawable.normal_background);

                if (listAppointmentPending.size() <= 0 && listAppointmentPendingFizic.size() <= 0) {
                    linearLayoutNoConsultation.setVisibility(View.VISIBLE);
                    rvPendingAppointment.setVisibility(View.GONE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                } else if(listAppointmentPending.size() > 0 && listAppointmentPendingFizic.size() <= 0 ){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.VISIBLE);
                }else if(listAppointmentPendingFizic.size() > 0 && listAppointmentPending.size() <= 0){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.VISIBLE);
                }else if(listAppointmentPendingFizic.size() > 0 && listAppointmentPending.size() > 0){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.VISIBLE);
                    rvPendingAppointmentFizic.setVisibility(View.VISIBLE);
                }

            }
        });

        tvCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCompleted.setBackgroundResource(R.drawable.pressed_background);
                tvPending.setBackgroundResource(R.drawable.normal_background);

                if (listAppointmentCompleted.size() <= 0 && listAppointmentCompletedFizic.size() <= 0) {
                    linearLayoutNoConsultation.setVisibility(View.VISIBLE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                } else if(listAppointmentCompleted.size() > 0 && listAppointmentCompletedFizic.size() <= 0 ){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointment.setVisibility(View.VISIBLE);
                    rvCompletedAppointmentFizic.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.GONE);
                }else if(listAppointmentCompletedFizic.size() > 0 && listAppointmentCompleted.size() <= 0){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.VISIBLE);
                    rvCompletedAppointment.setVisibility(View.GONE);
                    rvPendingAppointment.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                }else if(listAppointmentCompleted.size() > 0 && listAppointmentCompletedFizic.size() > 0){
                    linearLayoutNoConsultation.setVisibility(View.GONE);
                    rvCompletedAppointmentFizic.setVisibility(View.VISIBLE);
                    rvCompletedAppointment.setVisibility(View.VISIBLE);
                    rvPendingAppointment.setVisibility(View.GONE);
                    rvPendingAppointmentFizic.setVisibility(View.GONE);
                }

            }
        });

        return view;
    }
}