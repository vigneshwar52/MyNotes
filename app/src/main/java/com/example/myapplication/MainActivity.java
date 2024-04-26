package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.Adapters.NotesListAdapter;
import com.example.myapplication.Database.RoomDB;
import com.example.myapplication.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notesList = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerHome);
        fab = findViewById(R.id.fab_btn);

        database = RoomDB.getInstance(this);

        notesList = database.mainDAObj().getAll();
        updateRecycler(notesList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotesEditorActivity.class);
                startActivityForResult(intent,101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("notes");
                database.mainDAObj().insert(newNotes);
                notesList.clear();
                notesList.addAll(database.mainDAObj().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notesList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        notesListAdapter = new NotesListAdapter(this,notesList,notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }
    private  final NotesOnClickListener notesClickListener = new NotesOnClickListener() {
        @Override
        public void onClick(Notes notes) {

        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {

        }
    };
}