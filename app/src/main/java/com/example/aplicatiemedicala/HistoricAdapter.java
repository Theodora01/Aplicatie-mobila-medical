package com.example.aplicatiemedicala;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.HistoricViewHolder>{

    private List<Appointment> listApointment = new ArrayList<>();
    private Context context;
    private DatabaseReference referenceAppointment;
    private String idPacient;

    public HistoricAdapter(List<Appointment> listApointment, Context context) {
        this.listApointment = listApointment;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_view, parent, false);
        return new HistoricAdapter.HistoricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricViewHolder holder, int position) {

        Appointment appointment = listApointment.get(position);

        holder.tvNumeDoctor.setText(appointment.getNumeMedic());
        holder.tvSpecializareDoctor.setText(appointment.getSpecializare());
        holder.tvZiua.setText(appointment.getZiua());
        holder.tvOra.setText(appointment.getOra());
        holder.tvData.setText(appointment.getData());
        holder.tvTip.setText(appointment.getTipProgramare());


        if (isAppointmentPending(appointment)) {
            holder.btnAnulareOnline.setVisibility(View.VISIBLE);
            holder.btnAnulareOnline.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Cancellation")
                        .setMessage("Are you sure you want to cancel this appointment?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cancelAppointment(appointment.getIdProgramare());
                            listApointment.remove(position);
                            notifyItemRemoved(position);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        } else {
            holder.btnAnulareOnline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listApointment.size();
    }

    public static class HistoricViewHolder extends RecyclerView.ViewHolder{

        TextView tvNumeDoctor, tvSpecializareDoctor, tvData, tvZiua,tvOra, tvTip;
        Button btnAnulareOnline;
        public HistoricViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeDoctor = itemView.findViewById(R.id.tvNumeDoctor);
            tvSpecializareDoctor = itemView.findViewById(R.id.tvSpecializareDoctor);
            tvData = itemView.findViewById(R.id.tvDataProgramare);
            tvZiua = itemView.findViewById(R.id.tvZiuaProgramare);
            tvOra = itemView.findViewById(R.id.tvOraProgramare);
            tvTip = itemView.findViewById(R.id.tvTip);
            btnAnulareOnline =itemView.findViewById(R.id.btnAnulareProgramareOnline);
        }
    }
    private void cancelAppointment(String appointmentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointment").child(appointmentId);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Programming successfully cancelled", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error canceling the appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAppointmentPending(Appointment appointment) {
        String[] numeLuni = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Calendar currentDate = Calendar.getInstance();

        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        idPacient = firebaseUser.getUid();
        referenceAppointment = FirebaseDatabase.getInstance().getReference().child("appointment");

        String appointmentMonth = appointment.getLuna();
        String appointmentDate = appointment.getData();

        int ziuaInt = Integer.parseInt(appointmentDate);

        int lunaInt = -1;
        for (int i = 0; i < numeLuni.length; i++) {
            if (numeLuni[i].equals(appointmentMonth)) {
                lunaInt = i + 1;
                break;
            }
        }

        if (currentMonth == lunaInt) {
            if (currentDay == ziuaInt) {
                return false;
            } else if (currentDay > ziuaInt) {
                return false;
            } else if (currentDay < ziuaInt) {
                return true;
            }
        } else if (currentMonth < lunaInt) {
            return true;
        } else {
            return false;
        }
        return true;
    }
}
