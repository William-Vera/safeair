package com.example.safeairmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends Fragment {
    private RecyclerView recyclerView;
    private DatosAdapter adapter;
    private List<Datos> datosList;
    private DatabaseReference databaseReference;


    private DatabaseReference mDatabase;
    private TableLayout tableLayout;
    private ValueEventListener valueEventListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        datosList = new ArrayList<>();
        adapter = new DatosAdapter(datosList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("lecturas_peligrosas");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datosList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Datos datos = childSnapshot.getValue(Datos.class);
                    datosList.add(0, datos);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });

        return view;
    }

    private static class DatosAdapter extends RecyclerView.Adapter<DatosViewHolder> {
        private List<Datos> datosList;

        DatosAdapter(List<Datos> datosList) {
            this.datosList = datosList;
        }

        @NonNull
        @Override
        public DatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_datos, parent, false);
            return new DatosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DatosViewHolder holder, int position) {
            Datos datos = datosList.get(position);
            holder.bind(datos);
        }

        @Override
        public int getItemCount() {
            return datosList.size();
        }
    }

    private static class DatosViewHolder extends RecyclerView.ViewHolder {
        TextView descripcionTextView;
        TextView fechaHoraTextView;
        TextView lastValue1TextView;
        TextView lastValue2TextView;
        TextView lastValue3TextView;
        TextView lastValue4TextView;
        TextView lastValues2TextView;
        ImageView imageView;

        DatosViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            fechaHoraTextView = itemView.findViewById(R.id.fechaHoraTextView);
            lastValue1TextView = itemView.findViewById(R.id.lastValue1TextView);
            lastValue2TextView = itemView.findViewById(R.id.lastValue2TextView);
            lastValue3TextView = itemView.findViewById(R.id.lastValue3TextView);
            lastValue4TextView = itemView.findViewById(R.id.lastValue4TextView);
            lastValues2TextView = itemView.findViewById(R.id.lastValues2TextView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(Datos datos) {
            descripcionTextView.setText(datos.getDescripcion());

            String fechaHoraOriginal = datos.getFecha_hora();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date fechaHora = null;
            try {
                fechaHora = dateFormat.parse(fechaHoraOriginal);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm", new Locale("es", "ES"));
            String fechaHoraFormateada = dateFormatOutput.format(fechaHora);
            fechaHoraTextView.setText(fechaHoraFormateada);

            lastValue1TextView.setText(String.valueOf(datos.getLast_value1()));
            lastValue2TextView.setText(String.valueOf(datos.getLast_value2()));
            lastValue3TextView.setText(String.valueOf(datos.getLast_value3()));
            lastValue4TextView.setText(String.valueOf(datos.getLast_value4()));
            lastValues2TextView.setText(String.valueOf(datos.isLast_values2()));
        }
    }

    static class Datos {
        private String descripcion;
        private String fecha_hora;
        private int last_value1;
        private int last_value2;
        private double last_value3;
        private int last_value4;
        private boolean last_values2;

        public Datos() {

        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getFecha_hora() {
            return fecha_hora;
        }

        public int getLast_value1() {
            return last_value1;
        }

        public int getLast_value2() {
            return last_value2;
        }

        public double getLast_value3() {
            return last_value3;
        }

        public int getLast_value4() {
            return last_value4;
        }

        public boolean isLast_values2() {
            return last_values2;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu1, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mnuDownloadPDF);
        menuItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}