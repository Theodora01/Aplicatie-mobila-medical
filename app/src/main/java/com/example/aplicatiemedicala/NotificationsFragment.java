package com.example.aplicatiemedicala;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";
    private RecyclerView rvNotification;
    private DatabaseReference referenceAppointmentFizic;
    private DatabaseReference referenceAppointmentOnline;
    private FirebaseUser currentUser;
    private List<Appointment> appointmentOnline = new ArrayList<>();
    private List<AppointmentFizic> appointmentFizic= new ArrayList<>();
    private NotificatonAdapter adapter;
    private  String idPacient;

    public NotificationsFragment() {
    }


    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        idPacient = currentUser.getUid();

        referenceAppointmentOnline =FirebaseDatabase.getInstance().getReference().child("appointment");
        referenceAppointmentFizic = FirebaseDatabase.getInstance().getReference().child("appointmentFizic");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);

        rvNotification = view.findViewById(R.id.rvNotificationView);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificatonAdapter(appointmentOnline , appointmentFizic);
        rvNotification.setAdapter(adapter);

        setupNotifications();

        return view;
    }

    private void setupNotifications() {

        Log.d(TAG, "setupNotifications: called");
        referenceAppointmentOnline.orderByChild("idPacient").equalTo(idPacient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "referenceAppointmentOnline onDataChange: called");
                appointmentOnline.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment != null && isFutureAppointment(appointment)) {
                        appointmentOnline.add(appointment);
                        addNotification(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        referenceAppointmentFizic.orderByChild("idPacient").equalTo(idPacient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AppointmentFizic appointment = dataSnapshot.getValue(AppointmentFizic.class);
                    if (appointment != null && isFutureAppointment(appointment)) {
                        appointmentFizic.add(appointment);
                        addNotificationFizic(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotification(Appointment appointment) {
        setReminder(getContext(), appointment.getTimeInMillis(), "Reminder: Virtual programming", "You have a virtual appointment soon.");
        Log.d("NotificationsFragment", "Notification added for virtual appointment: " + appointment.getTimeInMillis());
    }
    private void addNotificationFizic(AppointmentFizic appointment) {
        setReminder(getContext(), appointment.getTimeInMillis(), "Reminder: Physical programming", "You have a physical appointment soon.");
        Log.d("NotificationsFragment", "Notification added for virtual appointment: " + appointment.getTimeInMillis());
    }
    public void setReminder(Context context, long timeInMillis, String title, String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        Log.d(TAG, "Reminder set for: " + timeInMillis);
    }
    private boolean isFutureAppointment(Appointment appointment) {
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "Checking if appointment is future: " + (appointment.getTimeInMillis() > currentTime));
        return appointment.getTimeInMillis() > currentTime;
    }

    private boolean isFutureAppointment(AppointmentFizic appointment) {
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "Appointment time: " + appointment.getTimeInMillis());
        Log.d(TAG, "Current time: " + currentTime);
        boolean isFuture = appointment.getTimeInMillis() > currentTime;
        Log.d(TAG, "Is future appointment: " + isFuture);
        return isFuture;
    }
}