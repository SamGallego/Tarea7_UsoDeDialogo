package com.example.tarea7_usodedialogo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import java.util.Locale;

public class MiFragmento extends DialogFragment {

    private OnTareaGuardadaListener listener;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private String asignaturaSeleccionada = "";

    public static MiFragmento nuevoFragmento(Deber tarea, int position) {
        MiFragmento fragment = new MiFragmento();
        Bundle args = new Bundle();

        if (tarea != null) {
            args.putString("titulo", tarea.getTitulo());
            args.putString("asignatura", tarea.getAsignatura());
            args.putString("descripcion", tarea.getDescripcion());
            args.putString("fecha", tarea.getFecha());
            args.putString("hora", tarea.getHora());
            args.putInt("position", position);
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

        Spinner spinner = view.findViewById(R.id.spinnerAsignaturas);
        EditText titulo = view.findViewById(R.id.editTextTitulo);
        TextView textViewFecha = view.findViewById(R.id.textViewFecha);
        TextView textViewHora = view.findViewById(R.id.textViewHora);

        // Configurar adaptador del Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.asignaturas_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int initialPosition = -1;

        if (getArguments() != null) {
            titulo.setText(getArguments().getString("titulo", ""));
            fechaSeleccionada = getArguments().getString("fecha", ""); // Inicializar fecha
            horaSeleccionada = getArguments().getString("hora", "");  // Inicializar hora
            asignaturaSeleccionada = getArguments().getString("asignatura", "");
            initialPosition = getArguments().getInt("position", -1);

            textViewFecha.setText(fechaSeleccionada.isEmpty() ? "Seleccionar fecha" : fechaSeleccionada);
            textViewHora.setText(horaSeleccionada.isEmpty() ? "Seleccionar hora" : horaSeleccionada);

            int spinnerPosition = adapter.getPosition(asignaturaSeleccionada);
            if (spinnerPosition >= 0) {
                spinner.setSelection(spinnerPosition);
            }
        }

        final int finalPosition = initialPosition;

        // Actualizar asignatura seleccionada cuando el usuario cambia el valor del Spinner
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

        // Selector de hora
        textViewHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view1, hourOfDay, minute) -> {
                        horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        textViewHora.setText(horaSeleccionada);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        builder.setView(view)
                .setTitle(getArguments() == null ? "Agregar Tarea" : "Editar Tarea")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    if (asignaturaSeleccionada.isEmpty() || titulo.getText().toString().trim().isEmpty() || fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Deber nuevaTarea = new Deber(
                            titulo.getText().toString(),
                            asignaturaSeleccionada,
                            "",
                            fechaSeleccionada,
                            horaSeleccionada,
                            "Pendiente"
                    );

                    listener.onTareaGuardada(nuevaTarea, finalPosition);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dismiss());

        return builder.create();
    }
}
