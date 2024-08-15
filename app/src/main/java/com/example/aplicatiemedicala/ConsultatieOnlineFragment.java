package com.example.aplicatiemedicala;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.animation.AnimationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultatieOnlineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultatieOnlineFragment extends Fragment implements CalendarAdapter.OnDateClickListener, TimeAdapter.OnTimeClickListener{

    private Spinner spCautaSpecializarea, spCautaDoctor,spLuna;
    private List<String> allSpecializari = new ArrayList<>();
    private List<String> months = new ArrayList<>();
    private TextView tvProgramMedic;
    private ConsultatieOnlineFragment consultatieOnlineFragment;
    private RecyclerView recyclerViewDates, recyclerViewTime;
    private String luna,textTipProgramare, dataSelectata, ziuaSelectata, timpSelectat, programMedic, numeMedic, specializareMedic,idDoctor,idPacient;
    private TextView tvNumeOnlineC, tvSpecializareOnlineC, tvPretTotal;
    private RadioGroup programareGrup;
    private RadioButton tipProgramare;
    private ImageView imgDoctor;
    private Button btnScheduleYourself;
    private Double tarif;
    private DatabaseReference referenceAppointment, databaseReference, referenceAppointmentFizic;
    private int selectedItemPositionData = RecyclerView.NO_POSITION, selectedItemPositionTime = RecyclerView.NO_POSITION;

    public ConsultatieOnlineFragment() {
        // Required empty public constructor
    }

    public static ConsultatieOnlineFragment newInstance(Doctor doctor) {
        ConsultatieOnlineFragment fragment = new ConsultatieOnlineFragment();
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
        View view=inflater.inflate(R.layout.fragment_consultatie_online, container, false);

        spCautaSpecializarea= view.findViewById(R.id.spCautaSpecializarea);
        spCautaDoctor = view.findViewById(R.id.spCautaDoctor);
        spLuna = view.findViewById(R.id.spMonths);
        tvProgramMedic= view.findViewById(R.id.tvProgramMedic);

        btnScheduleYourself = view.findViewById(R.id.btnScheduleYourself);

        imgDoctor = view.findViewById(R.id.imgDoctorOnlineC);
        tvNumeOnlineC = view.findViewById(R.id.tvNameOnlineC);
        tvSpecializareOnlineC = view.findViewById(R.id.tvSpecializationOnline);
        tvPretTotal = view.findViewById(R.id.tvPretTotal);
        programareGrup = view.findViewById(R.id.radioGroupOnline);

        programareGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                tipProgramare = view.findViewById(checkedId);

                if (tipProgramare != null) {
                    textTipProgramare = tipProgramare.getText().toString();
                }
            }
        });


        spCautaSpecializarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("doctors");
                reference.orderByChild("specializare").equalTo(item).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> fullNameDoctors=new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String nameDoctor = dataSnapshot.child("nume").getValue(String.class);
                            Doctor doctor = snapshot.getValue(Doctor.class);
                            consultatieOnlineFragment = ConsultatieOnlineFragment.newInstance(doctor);
                            fullNameDoctors.add(nameDoctor);
                        }

                        ArrayAdapter<String> adapterCautaDoctor = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, fullNameDoctors) {
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
                        adapterCautaDoctor.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        spCautaDoctor.setAdapter(adapterCautaDoctor);

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
        spCautaSpecializarea.setAdapter(adapterSpecializari);


        spCautaDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("doctors");
                reference.orderByChild("nume").equalTo(item).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                idDoctor = doctor.getId();
                                if(doctor.getGen().equals("Female")){
                                    imgDoctor.setImageResource(R.drawable.femaledoctor);
                                }else{
                                    imgDoctor.setImageResource(R.drawable.maledoctor);
                                }
                                if (doctor != null) {
                                    programMedic = doctor.getOrarLucru();
                                    numeMedic = doctor.getNume();
                                    specializareMedic = doctor.getSpecializare();
                                    tarif =0.0;

                                    tvProgramMedic.setText(programMedic);
                                    tvNumeOnlineC.setText(numeMedic);
                                    tvSpecializareOnlineC.setText(specializareMedic);

                                    tarif = doctor.getTarifConsultatie();

                                    if(textTipProgramare != null){
                                        if (textTipProgramare.equals("Control")) {
                                            tarif = doctor.getTarifControl();
                                        } else {
                                            tarif = doctor.getTarifConsultatie();
                                        }
                                        tvPretTotal.setText(String.valueOf(tarif));
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Toast.makeText(getContext(),item,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Select month
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        ArrayAdapter<String> adapterMonths = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, months) {
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
        adapterMonths.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spLuna.setAdapter(adapterMonths);


        //Select date
        recyclerViewDates = view.findViewById(R.id.rvDates);
        List<String> daysOfWeek = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        CalendarAdapter adapterCalendar = new CalendarAdapter(daysOfWeek, dates);
        recyclerViewDates.setAdapter(adapterCalendar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDates.setLayoutManager(layoutManager);


        adapterCalendar.setOnDateClickListener(new CalendarAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(int position, String dayOfWeek, String date) {
                ziuaSelectata =dayOfWeek;
                dataSelectata= date;

                selectedItemPositionData = position;
                adapterCalendar.notifyDataSetChanged();

                Log.d("Date selected", "Day: " + ziuaSelectata + ", Date: " + dataSelectata);
            }
        });

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        calendar.set(currentYear,currentMonth, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        for (int i = 1; i <= daysInMonth; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            daysOfWeek.add(sdf.format(calendar.getTime()));
            dates.add(String.valueOf(i));
        }
        spLuna.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //parent.setBackgroundResource(R.drawable.pressed_yellow);
                luna = parent.getItemAtPosition(position).toString();

                daysOfWeek.clear();
                dates.clear();
                Log.d("ConsultatieOnline", "Luna: " + luna );
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);

                calendar.set(currentYear,months.indexOf(luna), 1);
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
                for (int i = 1; i <= daysInMonth; i++) {
                    calendar.set(Calendar.DAY_OF_MONTH, i);
                    daysOfWeek.add(sdf.format(calendar.getTime()));
                    dates.add(String.valueOf(i));
                }
                adapterCalendar.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Select time
        recyclerViewTime = view.findViewById(R.id.rvTime);
        TimeAdapter timeAdapter = new TimeAdapter(getContext());
        recyclerViewTime.setAdapter(timeAdapter);
        recyclerViewTime.setLayoutManager(new GridLayoutManager(getContext(), 3));

        timeAdapter.setOnTimeClickListener(new TimeAdapter.OnTimeClickListener() {
            @Override
            public void onTimeClick(int position, Time time) {
                timpSelectat = String.format("%02d:00", time.getHour());

                selectedItemPositionTime = position;
                timeAdapter.notifyDataSetChanged();

                Log.d("TIME", "Timp selectat: " + timpSelectat);
            }

        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        idPacient = firebaseUser.getUid();

        referenceAppointmentFizic =FirebaseDatabase.getInstance().getReference().child("appointmentFizic");
        referenceAppointment = FirebaseDatabase.getInstance().getReference().child("appointment");
        btnScheduleYourself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                existAppointment(idPacient, specializareMedic, new AppointmentExist() {
                    @Override
                    public void onResult(boolean exists) {
                        if(exists){
                            Toast.makeText(getContext(), "You already have an appointment for this ongoing specialization", Toast.LENGTH_LONG).show();
                        }else{
                            saveAppointment(idDoctor, idPacient,ziuaSelectata,dataSelectata,timpSelectat,luna,textTipProgramare,numeMedic,tarif,specializareMedic);

                        }
                    }
                });

            }
        });

        return view;
    }

    public void saveAppointment(String idDoctor, String idPacient, String ziuaSelectata, String dataSelectata, String timpSelectat, String luna, String textTipProgramare, String numeMedic, Double tarif,String specializareMedic) {

        String idProgramare = referenceAppointment.child("appointment").push().getKey();
        referenceAppointment.orderByChild("idDoctor").equalTo(idDoctor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean available = true;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                        Appointment existingAppointment = appointmentSnapshot.getValue(Appointment.class);

                        if (existingAppointment != null) {
                            if (existingAppointment.getData().equals(dataSelectata) && existingAppointment.getOra().equals(timpSelectat) && existingAppointment.getLuna().equals(luna)) {
                                available = false;
                                break;
                            }
                        }
                    }
                }
                if(!available){
                    Toast.makeText(getContext(), "This date is already busy, please choose another.", Toast.LENGTH_LONG).show();
                }else if(available){
                    referenceAppointmentFizic.orderByChild("idDoctor").equalTo(idDoctor).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean availableFizic = true;
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    AppointmentFizic existingAppointmentFizic = dataSnapshot1.getValue(AppointmentFizic.class);

                                    if(existingAppointmentFizic != null){
                                        if(existingAppointmentFizic.getData().equals(dataSelectata) && existingAppointmentFizic.getOra().equals(timpSelectat) && existingAppointmentFizic.getLuna().equals(luna)){
                                            availableFizic = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(availableFizic){
                                Appointment appointment = new Appointment(idProgramare,idDoctor, idPacient, ziuaSelectata, dataSelectata, timpSelectat, luna, textTipProgramare, numeMedic, tarif,specializareMedic);
                                referenceAppointment.child(idProgramare).setValue(appointment)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Programming successfully recorded", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Failed programming", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "This date is already busy, please choose another.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error accessing the database.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface AppointmentExist{
        void onResult(boolean exists);
    }
    public void existAppointment(String idPacient,String specializareMedic, AppointmentExist callBack){

        referenceAppointment.orderByChild("idPacient").equalTo(idPacient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    String[] numeLuni = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                    Calendar currentDate = Calendar.getInstance();
                    int currentMonth = currentDate.get(Calendar.MONTH)+1;
                    int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Appointment existingAppointment = dataSnapshot.getValue(Appointment.class);
                            String appointmentMonth = existingAppointment.getLuna();
                            String appointmentDate = existingAppointment.getData();

                            int ziuaInt = Integer.parseInt(appointmentDate);
                            int lunaInt=-1;
                            for (int i = 0; i < numeLuni.length; i++) {
                                if (numeLuni[i].equals(appointmentMonth)) {
                                    lunaInt = i+1;
                                    break;
                                }
                            }
                            if (existingAppointment != null && existingAppointment.getSpecializare().equals(specializareMedic)) {
                                if(currentMonth < lunaInt || (currentMonth == lunaInt && currentDay <=ziuaInt)) {
                                    exists = true;
                                    break;
                                }
                            }
                        }
                    }
                    callBack.onResult(exists);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
        });
    }
    @Override
    public void onDateClick(int position, String dayOfWeek, String date) {
    }

    @Override
    public void onTimeClick(int position, Time time) {

    }
}

