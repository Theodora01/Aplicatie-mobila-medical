package com.example.aplicatiemedicala;

import static com.example.aplicatiemedicala.R.*;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListDoctorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListDoctorsFragment extends Fragment implements DoctorAdapter.OnDoctorClickListener{

    private RecyclerView recyclerViewCategorii,recyclerViewDoctori,recyclerSharedPref;
    private DoctorAdapter doctorAdapter;
    private DoctorAdapter favoriteDoctorAdapter;
    private List<String> specializari = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private FrameLayout frameListDoctor;
    TextView tvDoctor,tvFavorite;

    public ListDoctorsFragment() {
        // Required empty public constructor
    }

    public static ListDoctorsFragment newInstance(String param1, String param2) {
        ListDoctorsFragment fragment = new ListDoctorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(layout.fragment_list_doctors,container,false);

        coordinatorLayout = getActivity().findViewById(id.coordinatorMain);
        coordinatorLayout.setVisibility(View.VISIBLE);

        frameListDoctor=view.findViewById(id.frameListDoctor);
        frameListDoctor.setVisibility(View.VISIBLE);


        recyclerViewCategorii=view.findViewById(id.recyclerViewCategorii);
        recyclerViewDoctori=view.findViewById(id.recyclerViewDoctori);
        recyclerSharedPref=view.findViewById(id.recyclerSharedPref);
        recyclerSharedPref.setVisibility(View.GONE);

        specializari.add("Cardiology");
        specializari.add("Pulmonology");
        specializari.add("Pediatrics");
        specializari.add("Neurology");
        specializari.add("Psychiatry");
        specializari.add("Orthopaedics");
        specializari.add("Gynaecology");
        specializari.add("Internal Medicine");
        specializari.add("ENT");
        specializari.add("Ophthalmology");
        specializari.add("Anaesthesia");
        specializari.add("General Surgery");
        specializari.add("Infectious diseases");


        Animation animation=AnimationUtils.loadAnimation(getContext(), anim.button_scale);
        view.startAnimation(animation);


        //Click pe specializari
        SpecializariAdapter adapter = new SpecializariAdapter(specializari, new SpecializariAdapter.OnSpecializareClickListener() {
            @Override
            public void onSpecializareClick(String specializare) {


                DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference().child("doctors");
                doctorRef.orderByChild("specializare").equalTo(specializare).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Doctor> doctorsList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String doctorId = snapshot.getKey();
                            Doctor doctor = snapshot.getValue(Doctor.class);

                            doctorsList.add(doctor);
                        }
                    try {
                        doctorAdapter = new DoctorAdapter(doctorsList, getContext(), new DoctorAdapter.OnDoctorClickListener() {
                            @Override
                            public void onDoctorClick(Doctor doctor) {
                                ProfileDoctorFragment profileDoctorFragment = ProfileDoctorFragment.newInstance(doctor);
                                coordinatorLayout.setVisibility(View.VISIBLE);

                                if (profileDoctorFragment != null) {
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(id.frameLayoutProfileDoctor, profileDoctorFragment);

                                    fragmentTransaction.setCustomAnimations(anim.slide_in, anim.slide_out);
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
                        recyclerViewDoctori.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewDoctori.setAdapter(doctorAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });

        recyclerViewCategorii.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategorii.setLayoutManager(layoutManager);

        String specializareImplicita = "Cardiology";
        adapter.onSpecializareClick(specializareImplicita);

        tvDoctor=view.findViewById(id.tvDoctor);
        tvFavorite=view.findViewById(id.tvFavorite);

        tvDoctor.setBackgroundResource(drawable.pressed_background);
        tvFavorite.setBackgroundResource(drawable.normal_background);

        tvDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation=AnimationUtils.loadAnimation(getContext(), anim.button_scale);
                v.startAnimation(animation);
                tvDoctor.setBackgroundResource(drawable.pressed_background);
                tvFavorite.setBackgroundResource(drawable.normal_background);

                recyclerSharedPref.setVisibility(View.GONE);
                recyclerViewCategorii.setVisibility(View.VISIBLE);
                recyclerViewDoctori.setVisibility(View.VISIBLE);
            }
        });


        tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), anim.button_scale);
                v.startAnimation(animation);

                tvFavorite.setBackgroundResource(drawable.pressed_background);
                tvDoctor.setBackgroundResource(drawable.normal_background);

                recyclerViewCategorii.setVisibility(View.GONE);
                recyclerViewDoctori.setVisibility(View.GONE);
                recyclerSharedPref.setVisibility(View.VISIBLE);

                FirebaseAuth auth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser= auth.getCurrentUser();
                String idPacient = firebaseUser.getUid();

                DatabaseReference databaseFavorite = FirebaseDatabase.getInstance().getReference().child("favorite").child(idPacient);

                databaseFavorite.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Doctor> doctorsListFavorite = new ArrayList<>();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Doctor doctor = dataSnapshot.getValue(Doctor.class);

                            doctorsListFavorite.add(doctor);
                        }

                        favoriteDoctorAdapter =new DoctorAdapter(doctorsListFavorite,getContext());
                        recyclerSharedPref.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerSharedPref.setAdapter(favoriteDoctorAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        return view;
    }
    @Override
    public void onDoctorClick(Doctor doctor) {
        ProfileDoctorFragment profileDoctorFragment = ProfileDoctorFragment.newInstance(doctor);
        coordinatorLayout.setVisibility(View.VISIBLE);

        if(profileDoctorFragment != null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(id.frameLayoutProfileDoctor, profileDoctorFragment);

            fragmentTransaction.setCustomAnimations(anim.slide_in, anim.slide_out);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            Log.d("Fragment", "deschis " + doctor.getNume());
        }else{
            Log.d("Fragment", "nu poate fi deschis ");
        }

    }

    private static class SpecializariAdapter extends RecyclerView.Adapter<SpecializariAdapter.ViewHolder> {
        private List<String> specializations;
        private OnSpecializareClickListener specializareClickListener;

        SpecializariAdapter(List<String> specializations){
            this.specializations = specializations;
        }
        SpecializariAdapter(List<String> specializations,OnSpecializareClickListener listener) {
            this.specializations = specializations;
            this.specializareClickListener = listener;
        }

        public void onSpecializareClick(String specializareImplicita) {
            if (specializareClickListener != null) {
                specializareClickListener.onSpecializareClick(specializareImplicita);
            }
        }

        public interface OnSpecializareClickListener {
            void onSpecializareClick(String specializare);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(layout.categories_doctor, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String specialization = specializations.get(holder.getAdapterPosition());
            holder.specializationName.setText(specialization);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (specializareClickListener != null) {
                        specializareClickListener.onSpecializareClick(specialization);
                    }
                }
            });
            if (specialization.equals("Cardiology")) {
                holder.imageSpecializare.setImageResource(drawable.cardiologie);
            } else if (specialization.equals("Pulmonology")) {
                holder.imageSpecializare.setImageResource(drawable.pneumologie);
            }else if (specialization.equals("Internal Medicine")) {
                holder.imageSpecializare.setImageResource(drawable.medicinainterna);
            }else if (specialization.equals("Gynaecology")) {
                holder.imageSpecializare.setImageResource(drawable.ginecolofie);
            }else if (specialization.equals("ENT")) {
                holder.imageSpecializare.setImageResource(drawable.orl);
            }else if (specialization.equals("Ophthalmology")) {
                holder.imageSpecializare.setImageResource(drawable.oftamologie);
            }else if (specialization.equals("Anaesthesia")) {
                holder.imageSpecializare.setImageResource(drawable.anestezie);
            }else if (specialization.equals("Pediatrics")) {
                holder.imageSpecializare.setImageResource(drawable.pediatrie);
            }else if (specialization.equals("Orthopaedics")) {
                holder.imageSpecializare.setImageResource(drawable.ortopedie);
            }else if (specialization.equals("Neurology")) {
                holder.imageSpecializare.setImageResource(drawable.neurologie);
            }else if (specialization.equals("General Surgery")) {
                holder.imageSpecializare.setImageResource(drawable.chirurgie);
            }else if (specialization.equals("Psychiatry")) {
                holder.imageSpecializare.setImageResource(drawable.pshiatrie);
            }else if (specialization.equals("Infectious diseases")) {
                holder.imageSpecializare.setImageResource(drawable.boliinfectioase);
            }

        }

        @Override
        public int getItemCount() {
            return specializations.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView specializationName;
            ImageView imageSpecializare;
            RecyclerView recyclerViewDoctori;

            ViewHolder(View itemView) {
                super(itemView);
                specializationName = itemView.findViewById(id.tvCategoriiDoctori);
                imageSpecializare=itemView.findViewById(id.imgSpecializare);
                recyclerViewDoctori=itemView.findViewById(id.recyclerViewDoctori);
            }
        }
    }
}