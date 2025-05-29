package com.example.dailymate.Presentation.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailymate.Data.database.DBHelper;
import com.example.dailymate.Data.entity.Note;
import com.example.dailymate.Presentation.adapter.NoteAdapter;
import com.example.dailymate.Presentation.ui.AddEditActivity;
import com.example.dailymate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    NoteAdapter adapter;
    DBHelper dbHelper;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi elemen UI
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);
        dbHelper = new DBHelper(requireContext());

        // Ambil data dari database
        List<Note> list = dbHelper.getAllActivities();
        adapter = new NoteAdapter(requireContext(), list);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddEditActivity.class))
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateList(dbHelper.getAllActivities());
    }
}