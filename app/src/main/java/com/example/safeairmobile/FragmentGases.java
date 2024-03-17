package com.example.safeairmobile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentGases#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGases extends Fragment {

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

    public FragmentGases() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGases.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGases newInstance(String param1, String param2) {
        FragmentGases fragment = new FragmentGases();
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
        View view = inflater.inflate(R.layout.fragment_gases, container, false);
        lineChart = view.findViewById(R.id.lineChartt);
        setHasOptionsMenu(true);
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
                List<Float> values = getLastNSnapshotValues(snapshot, 7);
                updateChart(values);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private List<Float> getLastNSnapshotValues(DataSnapshot snapshot, int n) {
        List<Float> values = new ArrayList<>();
        int count = 0;
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            if (count >= snapshot.getChildrenCount() - n) {
                if (dataSnapshot.hasChild("last_value4")) {
                    float value = dataSnapshot.child("last_value4").getValue(Float.class);
                    values.add(value);
                }
            }
            count++;
        }
        return values;
    }

    private void setupDataUpdateRunnable() {
        handler = new Handler();
        dataUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                // Not needed for this implementation
            }
        };
    }

    private void updateChart(List<Float> values) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            entries.add(new Entry(i, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Valor Actual");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setCubicIntensity(0.2f);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

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

        if (!values.isEmpty()) {
            float currentValue = values.get(values.size() - 1);
            TextView currentValueTextView = getView().findViewById(R.id.currentValueTextView);
            currentValueTextView.setText(String.format(Locale.getDefault(), "Valor Actual: %.2f"+"ppm (Partes por millón)", currentValue));
        }

        int startColor = Color.parseColor("#80ADD8E6");
        int endColor = Color.TRANSPARENT;
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
        lineChart.setBackground(layerDrawable);

        lineChart.invalidate();
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

    private void generateAndSavePdf() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(lineChart.getWidth(), lineChart.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        lineChart.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        float titlePositionY = 80;
        canvas.drawText("Detección de gas/Cocina", pageInfo.getPageWidth() / 2, titlePositionY, paint);

        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMM 'a las' HH:mm", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        float dateTimePositionY = titlePositionY + 70;
        canvas.drawText(currentDateAndTime, pageInfo.getPageWidth() / 2, dateTimePositionY, paint);

        pdfDocument.finishPage(page);

        String fileName = "detección_de_gases.pdf";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();
            Toast.makeText(getContext(), "PDF descargado exitosamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuDownloadPDF) {
            generateAndSavePdf();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}