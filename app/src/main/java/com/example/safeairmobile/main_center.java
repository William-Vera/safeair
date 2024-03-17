package com.example.safeairmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class main_center extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navView;
    DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    private TextView textViewValues;
    private MenuItem notificacionesMenuItem;

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

        notificacionesMenuItem = navView.getMenu().findItem(R.id.mnuNotificacion);

        checkForNewNotifications();


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
        valueNames.put("last_value2", "Humedad (%RH)");
        valueNames.put("last_value3", "Temperatura (°C)");
        valueNames.put("last_value4", "Nivel de gases (ppm)");

        StringBuilder values = new StringBuilder();
        if (snapshot != null) {
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                String key = childSnapshot.getKey();
                if (valueNames.containsKey(key)) {
                    String valueName = valueNames.get(key);
                    String value = String.valueOf(childSnapshot.getValue());
                    values.append(valueName).append(": ").append(value);
                    if (key.equals("last_value3")) {
                        values.append("°C");
                    } else if (key.equals("last_value4")) {
                        values.append("ppm");
                    } else if (key.equals("last_value2")) {
                        values.append("%RH");
                    }else if (key.equals("last_value1")) {
                        values.append("ppm");
                    }
                    values.append("\n");
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
        //}else if(item.getItemId()==R.id.menu_seccion_4){
        //    fragment=new FragmentMedidas();
        //    hideElements();
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
        if (item.getItemId() == R.id.mnuNotificacion) {
            showDialogNotificaciones();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        //MenuItem menuItem = menu.findItem(R.id.mnuDownloadPDF);
        //menuItem.setVisible(false);
        return true;
    }
    //notificacioneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeesssssssssssssssssssssssssssss
    private class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesViewHolder> {
        private List<Notificacion> notificaciones;

        public NotificacionesAdapter(List<Notificacion> notificaciones) {
            this.notificaciones = notificaciones;
        }

        @NonNull
        @Override
        public NotificacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
            return new NotificacionesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificacionesViewHolder holder, int position) {
            Notificacion notificacion = notificaciones.get(position);
            holder.bind(notificacion);
        }

        @Override
        public int getItemCount() {
            return notificaciones.size();
        }
    }


    private static class NotificacionesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo;
        private TextView textViewDescripcion;
        private TextView textViewFechaHora;

        public NotificacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFechaHora = itemView.findViewById(R.id.textViewFechaHora);
        }

        public void bind(Notificacion notificacion) {
            textViewTitulo.setText(notificacion.titulo);
            textViewDescripcion.setText(notificacion.descripcion);
            textViewFechaHora.setText(notificacion.fecha_hora);
        }
    }
    static class Notificacion {
        String titulo;
        String descripcion;
        String fecha_hora;

        public Notificacion() {
            // Constructor vacío requerido por Firebase
        }

        public Notificacion(String titulo, String descripcion, String fecha_hora) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.fecha_hora = fecha_hora;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getFechaHora() {
            return fecha_hora;
        }

        public void setFechaHora(String fecha_hora) {
            this.fecha_hora = fecha_hora;
        }
    }

    private void showDialogNotificaciones() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notificaciones, null);

        RecyclerView recyclerViewNotificacionesDialog = dialogView.findViewById(R.id.recyclerViewNotificacionesDialog);
        recyclerViewNotificacionesDialog.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference notificacionesRef = FirebaseDatabase.getInstance().getReference("notificaciones");
        notificacionesRef.orderByKey().limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notificacion> notificaciones = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notificacion notificacion = dataSnapshot.getValue(Notificacion.class);
                    notificaciones.add(0, notificacion);
                    if (notificaciones.size() == 10) {
                        break;
                    }
                }
                NotificacionesAdapter notificacionesAdapter = new NotificacionesAdapter(notificaciones);
                recyclerViewNotificacionesDialog.setAdapter(notificacionesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Notificaciones");
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkForNewNotifications() {
        DatabaseReference notificacionesRef = FirebaseDatabase.getInstance().getReference("notificaciones");
        notificacionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Notificaciones", "onDataChange triggered");
                if (snapshot.exists() && notificacionesMenuItem != null) {
                    notificacionesMenuItem.setIcon(R.drawable.ic_point);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error
            }
        });
    }
}