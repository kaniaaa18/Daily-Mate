package com.example.dailymate.Presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailymate.Data.database.DBHelper;
import com.example.dailymate.Data.entity.Note;
import com.example.dailymate.Presentation.ui.AddEditActivity;
import com.example.dailymate.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    List<Note> list;
    DBHelper dbHelper;

    public NoteAdapter(Context context, List<Note> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DBHelper(context); // untuk hapus dari database
    }

    public void updateList(List<Note> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.time.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(model.getTime())));

        // Klik tombol Edit
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditActivity.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("activityId", model.getId());
            intent.putExtra("title", model.getTitle());
            intent.putExtra("description", model.getDescription());
            intent.putExtra("time", model.getTime());
            context.startActivity(intent);
        });

        // Klik tombol Hapus
        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteActivity(model.getId()); // hapus dari database
            list.remove(position);             // hapus dari list
            notifyItemRemoved(position);       // update RecyclerView
            notifyItemRangeChanged(position, list.size()); // untuk menjaga posisi
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, time;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}