package com.example.aplicatiemedicala;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificatonAdapter extends RecyclerView.Adapter<NotificatonAdapter.ViewHolderNotification> {

    private List<Appointment> appointmentOnline;
    private List<AppointmentFizic> appointmentFizic;

    public NotificatonAdapter(List<Appointment> appointmentOnline, List<AppointmentFizic> appointmentFizic) {
        this.appointmentOnline = appointmentOnline;
        this.appointmentFizic = appointmentFizic;
    }

    @NonNull
    @Override
    public ViewHolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificatonAdapter.ViewHolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificatonAdapter.ViewHolderNotification holder, int position) {
        if (position < appointmentOnline.size()) {
            Appointment appointment = appointmentOnline.get(position);
            holder.bindAppointment(appointment);
        } else {
            AppointmentFizic appointmentFizicItem = appointmentFizic.get(position - appointmentOnline.size());
            holder.bindAppointmentFizic(appointmentFizicItem);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentOnline.size() + appointmentFizic.size();
    }

    public static class ViewHolderNotification extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewMessage, textViewDoctor;
        public ViewHolderNotification(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDoctor = itemView.findViewById(R.id.textViewDoctor);
        }
        public void bindAppointment(Appointment appointment) {
            textViewTitle.setText("Reminder: Virtual programming");
            textViewMessage.setText("You have a virtual appointment soon.");
            textViewDoctor.setText(appointment.getNumeMedic());
        }

        public void bindAppointmentFizic(AppointmentFizic appointmentFizic) {
            textViewTitle.setText("Reminder: Physical programming");
            textViewMessage.setText("You have a physical appointment soon.");
            textViewDoctor.setText(appointmentFizic.getNumeMedic());
        }
    }
}
