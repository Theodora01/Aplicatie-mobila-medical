package com.example.aplicatiemedicala;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoricAdapterFizic extends RecyclerView.Adapter<HistoricAdapterFizic.HistoricFizicViewHolder>{
    private List<AppointmentFizic> listApointmentFizic = new ArrayList<>();
    private Context context;
    private DatabaseReference referenceAppointmentFizic;
    private String idPacient;

    public HistoricAdapterFizic(List<AppointmentFizic> listApointmentFizic, Context context) {
        this.listApointmentFizic = listApointmentFizic;
        this.context = context;
    }
    @NonNull
    @Override
    public HistoricFizicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_fizic,parent,false);
        return new HistoricFizicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricAdapterFizic.HistoricFizicViewHolder holder, int position) {
        AppointmentFizic appointment = listApointmentFizic.get(position);

        holder.tvNumeDoctor.setText(appointment.getNumeMedic());
        holder.tvSpecializareDoctor.setText(appointment.getSpecializare());
        holder.tvZiua.setText(appointment.getZiua());
        holder.tvOra.setText(appointment.getOra());
        holder.tvData.setText(appointment.getData());
        holder.tvTip.setText(appointment.getTipProgramare());

        if (isAppointmentPending(appointment)) {
            holder.btnAnulareFizic.setVisibility(View.VISIBLE);
            holder.btnAnulareFizic.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Cancellation")
                        .setMessage("Are you sure you want to cancel this appointment?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cancelAppointment(appointment.getIdProgramare());
                            listApointmentFizic.remove(position);
                            notifyItemRemoved(position);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        } else {
            holder.btnAnulareFizic.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listApointmentFizic.size();
    }

    public static class HistoricFizicViewHolder extends RecyclerView.ViewHolder{

        TextView tvNumeDoctor, tvSpecializareDoctor, tvData, tvZiua,tvOra, tvTip, tvLocatie;
        Button btnAnulareFizic;

        public HistoricFizicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeDoctor = itemView.findViewById(R.id.tvNumeDoctor);
            tvSpecializareDoctor = itemView.findViewById(R.id.tvSpecializareDoctor);
            tvData = itemView.findViewById(R.id.tvDataProgramare);
            tvZiua = itemView.findViewById(R.id.tvZiuaProgramare);
            tvOra = itemView.findViewById(R.id.tvOraProgramare);
            tvTip = itemView.findViewById(R.id.tvTip);
            tvLocatie =itemView.findViewById(R.id.tvLocatie);
            btnAnulareFizic=itemView.findViewById(R.id.btnAnulareProgramareFizic);
        }
    }
    private void cancelAppointment(String appointmentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointmentFizic").child(appointmentId);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Programming successfully cancelled", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error canceling the appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isAppointmentPending(AppointmentFizic appointment) {
        String[] numeLuni = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Calendar currentDate = Calendar.getInstance();

        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        idPacient = firebaseUser.getUid();
        referenceAppointmentFizic = FirebaseDatabase.getInstance().getReference().child("appointmentFizic");

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
