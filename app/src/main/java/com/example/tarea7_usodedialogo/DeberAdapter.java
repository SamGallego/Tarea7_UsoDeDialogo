package com.example.tarea7_usodedialogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DeberAdapter extends RecyclerView.Adapter<DeberAdapter.ViewHolder> {
    private final ArrayList<Deber> tareas;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public DeberAdapter(ArrayList<Deber> tareas) {
        this.tareas = tareas;
    }

    // Agregar tarea al adaptador
    public void addTarea(Deber tarea) {
        tareas.add(tarea);
        notifyItemInserted(tareas.size() - 1);
    }

    // Obtener tarea por posición
    public Deber getTarea(int position) {
        return tareas.get(position);
    }

    // Actualizar tarea en una posición específica
    public void updateTarea(int position, Deber tareaActualizada) {
        tareas.set(position, tareaActualizada);
        notifyItemChanged(position);
    }

    // Eliminar tarea por posición con confirmación
    public void removeTareaWithConfirmation(int position, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    tareas.remove(position);
                    notifyItemRemoved(position);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Asignar listener para clic corto
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Asignar listener para clic largo
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deber tarea = tareas.get(position);
        holder.bind(tarea);

        // Configurar clics en cada elemento
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView asignatura;
        private final TextView titulo;
        private final TextView fecha;
        private final TextView estado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            asignatura = itemView.findViewById(R.id.textAsignatura);
            titulo = itemView.findViewById(R.id.textTitulo);
            fecha = itemView.findViewById(R.id.textFecha);
            estado = itemView.findViewById(R.id.textEstado);
        }

        public void bind(Deber tarea) {
            asignatura.setText(tarea.getAsignatura());
            titulo.setText(tarea.getTitulo());

            // Formatear la fecha al formato dd/MM/yyyy
            String fechaOriginal = tarea.getFecha();
            String fechaFormateada = formatFecha(fechaOriginal);
            fecha.setText(fechaFormateada);

            estado.setText(tarea.getEstado());
        }

        // Método para formatear la fecha
        private String formatFecha(String fechaOriginal) {
            try {
                // Definir el formato original y el formato deseado
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                // Convertir y formatear la fecha
                Date date = originalFormat.parse(fechaOriginal);
                return desiredFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                // En caso de error, devolver la fecha original
                return fechaOriginal;
            }
        }
    }

    // Interfaz para clics simples
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Interfaz para clics largos
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
