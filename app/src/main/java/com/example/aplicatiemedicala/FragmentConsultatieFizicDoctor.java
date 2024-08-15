package com.example.aplicatiemedicala;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
 * Use the {@link FragmentConsultatieFizicDoctor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentConsultatieFizicDoctor extends Fragment {

    private RadioGroup programareGrup;
    private Spinner spMonthsConsultation;
    private String textTipProgramare;
    private RecyclerView rvDatesConsultationFizic,rvTimeConsultationFizic;
    private RadioButton tipProgramare;
    private List<String> months = new ArrayList<>();
    private String ziuaSelectata, dataSelectata, luna, timpSelectat,idPacient,idDoctor,specializareMedic,numeMedic,locatieProgramare;
    private double tarif;
    private int selectedItemPositionData = RecyclerView.NO_POSITION, selectedItemPositionTime = RecyclerView.NO_POSITION,ziuaInt;
    private DatabaseReference referenceAppointmentFizic,referenceAppointmentOnline;
    private Button btnAppointment;
    public FragmentConsultatieFizicDoctor() {

    }

    public static FragmentConsultatieFizicDoctor newInstance(Doctor doctor) {
        FragmentConsultatieFizicDoctor fragment = new FragmentConsultatieFizicDoctor();
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

        View view = inflater.inflate(R.layout.fragment_consultatie_fizic_doctor, container, false);

        programareGrup = view.findViewById(R.id.radioGroupFizic);
        spMonthsConsultation = view.findViewById(R.id.spMonthsConsultation);
        rvDatesConsultationFizic = view.findViewById(R.id.rvDatesConsultation);
        rvTimeConsultationFizic = view.findViewById(R.id.rvTimeConsultation);
        btnAppointment = view.findViewById(R.id.btnScheduleNowFizic);
        locatieProgramare = "Appointment in the clinic";

        programareGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                tipProgramare = view.findViewById(checkedId);

                if (tipProgramare != null) {
                    textTipProgramare = tipProgramare.getText().toString();
                    System.out.println(textTipProgramare);
                }
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            Doctor doctor = args.getParcelable("doctor");
            if (doctor != null) {
                TextView nameDoctor = view.findViewById(R.id.nameDoctorConsultationFizic);
                nameDoctor.setText(doctor.getNume());

                specializareMedic = doctor.getSpecializare();
                numeMedic =doctor.getNume();
                idDoctor = doctor.getId();
                tarif =0.0;

                TextView tvPretTotalConsultationFizic = view.findViewById(R.id.tvPretTotalConsultationFizic);
                if (textTipProgramare != null && textTipProgramare.equals("Consultation")) {

                    tvPretTotalConsultationFizic.setText(Double.toString(doctor.getTarifConsultatie()));
                    tarif = doctor.getTarifConsultatie();
                }else{
                    tvPretTotalConsultationFizic.setText(Double.toString(doctor.getTarifControl()));
                    tarif = doctor.getTarifControl();
                }

                ImageView imageProfileDoctor=view.findViewById(R.id.imageProfileDoctor);
                if(doctor.getGen().equals("Female")){
                    imageProfileDoctor.setImageResource(R.drawable.femaleprofile);
                }else{
                    imageProfileDoctor.setImageResource(R.drawable.maleprofile);
                }

                ImageView imgBackConsultationDoctoreDoctor =view.findViewById(R.id.btnBackConsultationDoctorFizic);
                imgBackConsultationDoctoreDoctor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backToConsultationFizic(v);
                    }
                });
            }
        }

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
        spMonthsConsultation.setAdapter(adapterMonths);

        //Select date
        List<String> daysOfWeek = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        CalendarAdapter adapterCalendarConsultationFizic = new CalendarAdapter(daysOfWeek, dates);
        rvDatesConsultationFizic.setAdapter(adapterCalendarConsultationFizic);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDatesConsultationFizic.setLayoutManager(layoutManager);

        adapterCalendarConsultationFizic.setOnDateClickListener(new CalendarAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(int position, String dayOfWeek, String date) {
                ziuaSelectata =dayOfWeek;
                dataSelectata= date;

                selectedItemPositionData = position;
                adapterCalendarConsultationFizic.notifyDataSetChanged();

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
        spMonthsConsultation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //parent.setBackgroundResource(R.drawable.pressed_yellow);
                luna = parent.getItemAtPosition(position).toString();

                daysOfWeek.clear();
                dates.clear();

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
                adapterCalendarConsultationFizic.notifyDataSetChanged();
                Log.d("LUNA", "Luna selectat: " + luna);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Select time
        TimeAdapter timeAdapter = new TimeAdapter(getContext());
        rvTimeConsultationFizic.setAdapter(timeAdapter);
        rvTimeConsultationFizic.setLayoutManager(new GridLayoutManager(getContext(), 3));

        timeAdapter.setOnTimeClickListener(new TimeAdapter.OnTimeClickListener() {
            @Override
            public void onTimeClick(int position, Time time) {
                timpSelectat = String.format("%02d:00", time.getHour());

                selectedItemPositionTime = position;
                timeAdapter.notifyDataSetChanged();

                Log.d("TIME", "Timp selectat: " + timpSelectat);
            }

        });

        //Appointment
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        idPacient = firebaseUser.getUid();

        referenceAppointmentOnline =FirebaseDatabase.getInstance().getReference().child("appointment");
        referenceAppointmentFizic = FirebaseDatabase.getInstance().getReference().child("appointmentFizic");
        btnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existAppointment(idPacient, specializareMedic, new FragmentConsultatieFizicDoctor.AppointmentExistFizic() {
                    @Override
                    public void onResult(boolean exists) {
                        if(exists){
                            Toast.makeText(getContext(), "You already have an appointment for this ongoing specialization", Toast.LENGTH_LONG).show();
                        }else{
                            saveAppointment(idDoctor, idPacient,ziuaSelectata,dataSelectata,timpSelectat,luna,textTipProgramare,numeMedic,tarif,specializareMedic,locatieProgramare);
                        }
                    }
                });
            }
        });
        return view;
    }

    public interface AppointmentExistFizic{
        void onResult(boolean exists);
    }
    public void existAppointment(String idPacient,String specializareMedic, FragmentConsultatieFizicDoctor.AppointmentExistFizic callBack){

        referenceAppointmentFizic.orderByChild("idPacient").equalTo(idPacient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                String[] numeLuni = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                Calendar currentDate = Calendar.getInstance();
                int currentMonth = currentDate.get(Calendar.MONTH)+1;
                int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AppointmentFizic existingAppointment = dataSnapshot.getValue(AppointmentFizic.class);
                        String appointmentMonth = existingAppointment.getLuna();
                        String appointmentDate = existingAppointment.getData();

                        ziuaInt = Integer.parseInt(appointmentDate);
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
    public void saveAppointment(String idDoctor, String idPacient, String ziuaSelectata, String dataSelectata, String timpSelectat, String luna, String textTipProgramare, String numeMedic, Double tarif,String specializareMedic,String locatieProgramare) {

        String idProgramare = referenceAppointmentFizic.child("appointmentFizic").push().getKey();
        referenceAppointmentFizic.orderByChild("idDoctor").equalTo(idDoctor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean available = true;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                        AppointmentFizic existingAppointment = appointmentSnapshot.getValue(AppointmentFizic.class);

                        if (existingAppointment != null) {
                            Log.d("Luna programarii",existingAppointment.getLuna());
                            if (existingAppointment.getData().equals(dataSelectata) && existingAppointment.getOra().equals(timpSelectat) && existingAppointment.getLuna().equals(luna)) {
                                available = false;
                                break;
                            }
                        }
                    }
                }if(!available){
                    Toast.makeText(getContext(), "This date is already busy, please choose another.", Toast.LENGTH_LONG).show();
                }else if(available){
                    referenceAppointmentOnline.orderByChild("idDoctor").equalTo(idDoctor).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean availableOnline = true;
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    Appointment existingAppointmentOnline = dataSnapshot1.getValue(Appointment.class);

                                    if (existingAppointmentOnline != null) {
                                        Log.d("Luna programarii",existingAppointmentOnline.getLuna());
                                        if (existingAppointmentOnline.getData().equals(dataSelectata) && existingAppointmentOnline.getOra().equals(timpSelectat) &&existingAppointmentOnline.getLuna().equals(luna)) {
                                            availableOnline = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (availableOnline) {
                                AppointmentFizic appointment = new AppointmentFizic(idProgramare, idDoctor, idPacient, ziuaSelectata, dataSelectata, timpSelectat, luna, textTipProgramare, numeMedic, tarif, specializareMedic, locatieProgramare);
                                referenceAppointmentFizic.child(idProgramare).setValue(appointment)
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
                        public void onCancelled (@NonNull DatabaseError error){

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

    public void backToConsultationFizic(View view) {
        FragmentManager fragmentManager= requireActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.frameLayoutConsultatieDoctor, new ConsultatieFizicFragment())
                .setCustomAnimations(R.anim.slide_out,R.anim.slide_in)
                .addToBackStack(null)
                .commit();
    }
}