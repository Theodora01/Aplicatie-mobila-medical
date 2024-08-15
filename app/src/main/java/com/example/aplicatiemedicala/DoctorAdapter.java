package com.example.aplicatiemedicala;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>{
    private List<Doctor> listaDoctori=new ArrayList<>();
    private Context context;
    private OnDoctorClickListener doctorClickListener;
    private DatabaseReference databaseReference;
    public DoctorAdapter() {
    }

    public DoctorAdapter(List<Doctor> listaDoctori, Context context) {
        this.listaDoctori = listaDoctori;
        this.context = context;
    }

    public DoctorAdapter(List<Doctor> listaDoctori, Context context, OnDoctorClickListener doctorClickListener) {
        this.listaDoctori = listaDoctori;
        this.context = context;
        this.doctorClickListener=doctorClickListener;
    }
    public interface OnDoctorClickListener{
        void onDoctorClick(Doctor doctor);
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_view, parent, false);
        return new DoctorViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
         Doctor doctor=listaDoctori.get(position);
         String idDoctor = doctor.getId();

         FirebaseAuth auth = FirebaseAuth.getInstance();
         FirebaseUser firebaseUser = auth.getCurrentUser();
         String idPacient = firebaseUser.getUid();

         databaseReference= FirebaseDatabase.getInstance().getReference().child("favorite").child(idPacient);

         holder.textViewName.setText(doctor.getNume());
         holder.textViewSpecialization.setText(doctor.getSpecializare());
         holder.edRating.setText(String.valueOf(doctor.getStele()));


        if (doctor.getGen().equals("Female")) {
            holder.imageDoctor.setImageResource(R.drawable.femaledoctor);
        } else if(doctor.getGen().equals("Male")){
            holder.imageDoctor.setImageResource(R.drawable.maledoctor);
        }

        if(isDoctorFavorite(idDoctor)) {
            holder.imageStarFavorite.setImageResource(R.drawable.baseline_star_24);
        }else{
            holder.imageStarFavorite.setImageResource(R.drawable.baseline_star_border_24);
        }

        holder.imageStarFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDoctorFavorite(idDoctor)){
                    holder.imageStarFavorite.setImageResource(R.drawable.baseline_star_border_24);

                    removeDoctorFavorite(idDoctor);

                    databaseReference.child(idDoctor).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(v.getContext(), "Medicul a fost eliminat de la favorite", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(v.getContext(), "Eroare la elimarea medicului", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    holder.imageStarFavorite.setImageResource(R.drawable.baseline_star_24);

                    salvareDoctorFavorite(idDoctor);

                    databaseReference.child(idDoctor).setValue(doctor)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(v.getContext(), "Medicul a fost adăugat la favorite", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "Medicul nu a fost adăugat la favorite", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DoctorAdapter", "Doctor clicked: " + doctor.getNume());

                Animation animation= AnimationUtils.loadAnimation(v.getContext(), R.anim.button_scale);
                v.startAnimation(animation);

                if (doctorClickListener != null) {
                    doctorClickListener.onDoctorClick(doctor);
                }
                else{
                    Log.e("Doctor Adapter", "Error opening ProfileDoctorFragment: fragment is null");
                }
            }
        });


    }
    private void salvareDoctorFavorite(String idDoctor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(idDoctor, true);
        editor.apply();
    }
    private void removeDoctorFavorite(String idDoctor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(idDoctor);
        editor.apply();
    }
    private boolean isDoctorFavorite(String idDoctor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(idDoctor, false);
    }
    @Override
    public int getItemCount() {
        return listaDoctori.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{
        ImageView imageDoctor;
        TextView textViewName, textViewSpecialization;

        TextView edRating;
        ImageView imageStarFavorite;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDoctor = itemView.findViewById(R.id.imageDoctor);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSpecialization = itemView.findViewById(R.id.textViewSpecialization);
            edRating=itemView.findViewById(R.id.etRating);
            imageStarFavorite=itemView.findViewById(R.id.checkBoxFavorite);
        }
    }

}

