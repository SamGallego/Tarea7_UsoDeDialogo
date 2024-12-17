package com.example.tarea7_usodedialogo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MiFragmento extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflar el layout del diálogo con el xml que hemos creado antes
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.fragment_mi_fragmento, null));

        // Establece el título
        builder.setTitle("Mi diálogo");

        // Añadir botones de aceptar y cancelar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Ejecutar al pulsar el botón aceptar
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Ejecutar al pulsar el botón aceptar
                        MiFragmento.this.getDialog().cancel();
                    }
                });

        // Devuelve el diálogo
        return builder.create();

    }
}