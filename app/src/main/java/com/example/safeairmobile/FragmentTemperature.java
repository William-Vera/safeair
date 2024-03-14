package com.example.safeairmobile;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTemperature#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTemperature extends Fragment {
    private LineChart lineChart;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private Entry lastEntry;
    private Handler handler;
    private Runnable dataUpdateRunnable;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentTemperature() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTemperature.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTemperature newInstance(String param1, String param2) {
        FragmentTemperature fragment = new FragmentTemperature();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        lineChart = view.findViewById(R.id.lineChartt);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFirebaseListener();
        setupDataUpdateRunnable();
    }

    private void setupFirebaseListener() {
        databaseReference = FirebaseDatabase.getInstance().getReference("lecturas_sensor");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot lastSnapshot = getLastSnapshot(snapshot);
                if (lastSnapshot != null && lastSnapshot.hasChild("last_value3")) {
                    float value = lastSnapshot.child("last_value3").getValue(Float.class);
                    lastEntry = new Entry(0, value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void setupDataUpdateRunnable() {
        handler = new Handler();
        dataUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateChart();
                handler.postDelayed(this, 1000); // Repetir cada 5 segundos
            }
        };
        handler.postDelayed(dataUpdateRunnable, 1000); // Iniciar después de 5 segundos
    }

    private DataSnapshot getLastSnapshot(DataSnapshot snapshot) {
        DataSnapshot lastSnapshot = null;
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            lastSnapshot = dataSnapshot;
        }
        return lastSnapshot;
    }

    private void updateChart() {
        if (lastEntry != null) {
            List<Entry> entries = new ArrayList<>();
            entries.add(lastEntry);

            LineDataSet dataSet = new LineDataSet(entries, "Valor Actual");
            dataSet.setMode(LineDataSet.Mode.LINEAR);
            dataSet.setDrawCircles(true);
            dataSet.setCircleRadius(5f);
            dataSet.setDrawCircleHole(false);
            dataSet.setCubicIntensity(0.2f);

            dataSet.setValueTextSize(14f); // Tamaño del texto
            dataSet.setValueTextColor(Color.BLACK); // Color del texto

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.setExtraLeftOffset(0f);
            lineChart.setExtraRightOffset(20f);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);

            lineChart.getDescription().setEnabled(false);
            lineChart.animateX(1000);
            lineChart.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        if (handler != null && dataUpdateRunnable != null) {
            handler.removeCallbacks(dataUpdateRunnable);
        }
    }
}