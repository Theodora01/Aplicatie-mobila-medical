package com.example.aplicatiemedicala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private CardView cardViewBarUp,cardViewSchedule;
    private BottomNavigationView bottomNavigationView;
    private TextView tvNameMain,tvIntrebare;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private GridLayout gridLayoutMain;
    private Button btnBookNow;
    LinearLayout linearLayout;
    CoordinatorLayout coordinatorLayout;
    private MenuItem itemSideBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //elementele din Main Page
        linearLayout=findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.VISIBLE);

        gridLayoutMain=findViewById(R.id.gridLayoutMain);
        gridLayoutMain.setVisibility(View.VISIBLE);

        cardViewSchedule=findViewById(R.id.cardViewSchedule);
        cardViewSchedule.setVisibility(View.VISIBLE);

        tvIntrebare=findViewById(R.id.tvIntrebare);
        tvIntrebare.setVisibility(View.VISIBLE);

        cardViewBarUp=findViewById(R.id.cardViewUpMain);
        cardViewBarUp.setVisibility(View.VISIBLE);

        coordinatorLayout=findViewById(R.id.coordinatorMain);
        coordinatorLayout.setVisibility(View.VISIBLE);
        btnBookNow = findViewById(R.id.btnBookNow);

        //Seteza numele utilizator
        tvNameMain=findViewById(R.id.tvNameMain);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("users").child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    tvNameMain.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("RegisterActivity", "loadUsername:onCancelled", databaseError.toException());
            }
        });

        //Side bar

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView=findViewById(R.id.navBottomView);


        drawerLayout=findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                coordinatorLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ImageButton btnSideBar=findViewById(R.id.btnSideBar);
        btnSideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordinatorLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                openSidebar(v);
            }
        });

        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_scale);
                v.startAnimation(animation);

                cardViewBarUp.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);

                ConsultatieFizicFragment fragment= new ConsultatieFizicFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
                fragmentTransaction.commit();
            }
        });
    }

    //SIDE BAR && FRAGMENTS
    public void openSidebar(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    private void replaceFragment(Fragment fragment){

        linearLayout.setVisibility(View.GONE);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_item_home) {
            Intent intent = new Intent(this, MainPage.class);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.menu_item_profile) {
            replaceFragment(new ProfileFragment());
        } else if (itemId == R.id.menu_item_settings) {
            replaceFragment(new SettingsFragment());
        } else if(itemId == R.id.menu_item_logout){
            Intent intent = new Intent(this, StartPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }


    //BUTTONS
    public void btnDoctors(View view) {
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.button_scale);
        view.startAnimation(animation);

        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        ListDoctorsFragment fragment= new ListDoctorsFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();

    }

    public void btnAnalize(View view) {
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.button_scale);
        view.startAnimation(animation);

        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        NewsFragment fragment= new NewsFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }

    public void btnIstoric(View view) {
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.button_scale);
        view.startAnimation(animation);

        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        HistoricFragment fragment= new HistoricFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }

    public void btnConsultatieOnline(View view) {
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.button_scale);
        view.startAnimation(animation);

        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        coordinatorLayout.setVisibility(View.VISIBLE);

        ConsultatieOnlineFragment fragment= new ConsultatieOnlineFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }

    public void btnHome(MenuItem item) {
            item.setChecked(true);
            Intent intent = new Intent(getApplicationContext(), MainPage.class);
            startActivity(intent);
    }

    public void btnNotifications(MenuItem item) {
        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        item.setChecked(true);
        NotificationsFragment fragment= new NotificationsFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }

    public void btnNews(MenuItem item) {
        cardViewBarUp.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        item.setChecked(true);
        NewsFragment fragment= new NewsFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayoutMain,fragment);
        fragmentTransaction.commit();
    }
}