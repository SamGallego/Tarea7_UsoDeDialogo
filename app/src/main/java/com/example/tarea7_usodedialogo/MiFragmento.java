package com.example.tarea7_usodedialogo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MiFragmento extends DialogFragment {

    private OnTareaGuardadaListener listener;
    private String fechaSeleccionada = "";
    private String asignaturaSeleccionada = "";

    // Método estático para crear un fragmento para edición de tareas
    public static MiFragmento nuevoFragmento(Deber tarea, int position) {
        MiFragmento fragment = new MiFragmento();
        Bundle args = new Bundle();

        // Pasar los datos de la tarea al fragmento mediante un Bundle
        if (tarea != null) {
            args.putString("titulo", tarea.getTitulo());
            args.putString("asignatura", tarea.getAsignatura());
            args.putString("descripcion", tarea.getDescripcion());
            args.putString("fecha", tarea.getFecha());
            args.putString("hora", tarea.getHora());
            args.putInt("position", position); // Posición en el adaptador
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTareaGuardadaListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTareaGuardadaListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_mi_fragmento, null);

        // Configurar Spinner de asignaturas
        Spinner spinner = view.findViewById(R.id.spinnerAsignaturas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.asignaturas_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Configurar campos de entrada
        EditText titulo = view.findViewById(R.id.editTextTitulo);
        TextView textViewFecha = view.findViewById(R.id.textViewFecha);

        // Cargar datos si se está editando una tarea
        int position = -1;
        if (getArguments() != null) {
            titulo.setText(getArguments().getString("titulo", ""));
            textViewFecha.setText(getArguments().getString("fecha", ""));
            asignaturaSeleccionada = getArguments().getString("asignatura", "");

            // Configurar el Spinner para que seleccione la asignatura existente
            int spinnerPosition = adapter.getPosition(asignaturaSeleccionada);
            if (spinnerPosition >= 0) {
                spinner.setSelection(spinnerPosition);
            }

            position = getArguments().getInt("position", -1);
        }

        // Manejar selección de asignaturas
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                asignaturaSeleccionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                asignaturaSeleccionada = "";
            }
        });

        // Selector de fecha
        textViewFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
                        textViewFecha.setText(fechaSeleccionada);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        int finalPosition = position;
        builder.setView(view)
                .setTitle(getArguments() == null ? "Agregar Tarea" : "Editar Tarea")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Validar campos
                    if (asignaturaSeleccionada.isEmpty() || titulo.getText().toString().trim().isEmpty() || fechaSeleccionada.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Crear una nueva tarea o actualizarla
                    Deber nuevaTarea = new Deber(
                            titulo.getText().toString(),
                            asignaturaSeleccionada,
                            "",
                            fechaSeleccionada,
                            "",
                            "Pendiente"
                    );

                    // Enviar tarea al listener con la posición
                    listener.onTareaGuardada(nuevaTarea, finalPosition);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dismiss());

        return builder.create();
    }
}
