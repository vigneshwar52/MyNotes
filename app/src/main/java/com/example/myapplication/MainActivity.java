package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notesList = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab;
    SearchView searchView;
    Notes selectedNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerHome);
        fab = findViewById(R.id.fab_btn);

        searchView = findViewById(R.id.searchView);

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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Notes> filterNotesList = new ArrayList<>();
        for(Notes singleNote:notesList){
            if(singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())||singleNote.getDescription().toLowerCase().contains(newText.toLowerCase())){
                filterNotesList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filterNotesList);
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
        } else if (requestCode == 102) {
            if(resultCode == Activity.RESULT_OK) {
                Notes newNotes = (Notes) data.getSerializableExtra("notes");
                database.mainDAObj().update(newNotes.getID(),newNotes.getTitle(),newNotes.getDescription());
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
            Intent intent = new Intent(MainActivity.this,NotesEditorActivity.class);
            intent.putExtra("old_data",notes);
            startActivityForResult(intent,102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      if(item.getItemId() == R.id.pin){
                if (selectedNote.isPinned()) {
                    database.mainDAObj().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "unpinned!", Toast.LENGTH_SHORT).show();
                } else {
                    database.mainDAObj().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "pinned!", Toast.LENGTH_SHORT).show();
                }
                notesList.clear();
                notesList.addAll(database.mainDAObj().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
        }
      else{
          database.mainDAObj().delete(selectedNote);
          Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
          notesList.remove(selectedNote);
          notesListAdapter.notifyDataSetChanged();
          return true;
      }
    }
}