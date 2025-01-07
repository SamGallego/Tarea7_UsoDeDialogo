package com.example.tarea7_usodedialogo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnTareaGuardadaListener {


    private RecyclerView recyclerView;
    private DeberAdapter adapter;
    private FloatingActionButton floatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración de RecyclerView y FloatingActionButton
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeberAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        floatBtn = findViewById(R.id.floatingActionButton);
        floatBtn.setOnClickListener(v -> {
            MiFragmento dialogFragment = new MiFragmento();
            dialogFragment.show(getSupportFragmentManager(), "AgregarTarea");
        });

        // Configuración de clic largo para mostrar BottomSheetDialog
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Acción para clic corto
            }

            @Override
            public void onLongItemClick(View view, int position) {
                mostrarBottomSheetDialog(position);
            }
        }));
    }

    @Override
    public void onTareaGuardada(Deber nuevaTarea, int position) {
        if (position == -1) {
            // Crear nueva tarea
            adapter.addTarea(nuevaTarea);
            Toast.makeText(this, "Tarea creada", Toast.LENGTH_SHORT).show();
        } else {
            // Editar tarea existente
            adapter.updateTarea(position, nuevaTarea);
            Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarBottomSheetDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Configurar las opciones del BottomSheetDialog
        bottomSheetView.findViewById(R.id.marcar_completado).setOnClickListener(v -> {
            // Marcar como completado
            Deber tarea = adapter.getTarea(position);
            tarea.setEstado("Completado");
            adapter.notifyItemChanged(position);
            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.editar).setOnClickListener(v -> {
            // Editar la tarea
            Deber tarea = adapter.getTarea(position);
            MiFragmento editarFragmento = MiFragmento.nuevoFragmento(tarea, position); // Pasa la posición
            editarFragmento.show(getSupportFragmentManager(), "EditarTarea");
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.eliminar).setOnClickListener(v -> {
            // Eliminar la tarea
            adapter.removeTareaWithConfirmation(position, recyclerView);
            Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}
