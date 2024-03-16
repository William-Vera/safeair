package com.example.safeairmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class main_center extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navView;
    DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    private TextView textViewValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_center);

        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        toolbar.setTitle("SafeAir");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        textViewValues = findViewById(R.id.textViewValues);

        // Configurar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("lecturas_sensor");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot lastSnapshot = getLastSnapshot(snapshot);

                String values = getLastValues(lastSnapshot);

                String dateTime = lastSnapshot.child("fecha_hora").getValue(String.class);

                String formattedDateTime = formatDateAndTime(dateTime);

                //textViewValues.setText("Fecha y hora: " + formattedDateTime + "\n" + values );
                textViewValues.setText(values + "\n");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    private String formatDateAndTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = inputFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return outputFormat.format(date);
        } else {
            return "";
        }
    }

    private DataSnapshot getLastSnapshot(DataSnapshot snapshot) {
        DataSnapshot lastSnapshot = null;
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            lastSnapshot = dataSnapshot;
        }
        return lastSnapshot;
    }
    private String getLastValues(DataSnapshot snapshot) {
        Map<String, String> valueNames = new HashMap<>();
        valueNames.put("last_value1", "Calidad del aire");
        valueNames.put("last_value2", "Humedad");
        valueNames.put("last_value3", "Temperatura");
        valueNames.put("last_value4", "Nivel de gases");

        StringBuilder values = new StringBuilder();
        if (snapshot != null) {
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                String key = childSnapshot.getKey();
                if (valueNames.containsKey(key)) {
                    String valueName = valueNames.get(key);
                    String value = String.valueOf(childSnapshot.getValue());
                    values.append(valueName).append(": ").append(value).append("\n");
                }
            }
        }
        return values.toString();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if(item.getItemId()==R.id.menu_seccion_1) {
            fragment = new FragmentAir();
            hideElements();
        }else if(item.getItemId()==R.id.menu_seccion_4){
            fragment=new FragmentMedidas();
            hideElements();
        }else if(item.getItemId()==R.id.menu_seccion_2){
            fragment=new FragmentGases();
            hideElements();
        }else if(item.getItemId()==R.id.menu_seccion_3){
            fragment=new FragmentTemperature();
            hideElements();
        }else if(item.getItemId()==R.id.menu_seccion_5){
            fragment=new FragmentHistory();
            hideElements();
        }else if(item.getItemId()==R.id.menu_seccion_6){
            fragment=new FragmentHumedad();
            hideElements();
        } else if(item.getItemId()==R.id.menu_seccion_7){
            startActivity(new Intent(main_center.this, main_center.class));
        }

        if(fragment!=null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }
        drawerLayout.closeDrawers();
        return true;
    }
    private void hideElements() {
        LinearLayout linearLayout = findViewById(R.id.layoutToHide);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                DrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }
}