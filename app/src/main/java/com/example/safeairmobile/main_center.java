package com.example.safeairmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class main_center extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navView;
    DrawerLayout drawerLayout;
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if(item.getItemId()==R.id.menu_seccion_1) {
            fragment = new FragmentAir();
        }else if(item.getItemId()==R.id.menu_seccion_4){
            fragment=new FragmentMedidas();
        }else if(item.getItemId()==R.id.menu_seccion_2){
            fragment=new FragmentGases();
        }else if(item.getItemId()==R.id.menu_seccion_3){
            fragment=new FragmentTemperature();
        }else if(item.getItemId()==R.id.menu_seccion_5){
            fragment=new FragmentHistory();
        }else if(item.getItemId()==R.id.menu_seccion_6){
            fragment=new FragmentHumedad();
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