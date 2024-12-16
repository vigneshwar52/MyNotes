package com.application.MyNotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.application.MyNotes.Adapters.NotesListAdapter;
import com.application.MyNotes.Database.RoomDB;
import com.application.MyNotes.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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
    ImageButton menuBtn;
    List<Notes> filterNotesList;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTESEDITORNEW = 0x01;
    private static final int NOTESEDITORUPDATE = 0x02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();

        database = com.application.MyNotes.Database.RoomDB.getInstance(this);
        notesList = database.mainDAObj().getAll();

        updateRecycler(notesList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotesEditorActivity.class);
                startActivityForResult(intent,NOTESEDITORNEW);
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
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                clearFilterAndUpdateData();
                return false;
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    private void getViews() {
        fab = findViewById(R.id.fab_btn);
        searchView = findViewById(R.id.searchView);
        menuBtn = findViewById(R.id.menu_btn);
        recyclerView = findViewById(R.id.recyclerHome);
    }

    void showMenu(){
        android.widget.PopupMenu popupMenu  = new android.widget.PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    protected void onResume() {
        updateRecycler(notesList);
        super.onResume();
    }

    private void clearFilterAndUpdateData() {
        filterNotesList.clear();
        updateRecycler(notesList);
    }

    private void filter(String newText) {
        filterNotesList = new ArrayList<>();
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

        if(requestCode == NOTESEDITORNEW){
            if(resultCode == Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("notes");
                database.mainDAObj().insert(newNotes);
                //notesList.clear();
                //notesList.addAll(database.mainDAObj().getAll());
                notesList.add(newNotes);
            }
        } else if (requestCode == NOTESEDITORUPDATE) {
            if(resultCode == Activity.RESULT_OK) {
                Notes newNotes = (Notes) data.getSerializableExtra("notes");
                assert newNotes != null;
                database.mainDAObj().update(newNotes.getID(),newNotes.getTitle(),newNotes.getDescription());
                notesList.clear();
                notesList.addAll(database.mainDAObj().getAll());
            }
        }
        notesListAdapter.notifyDataSetChanged();
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
            startActivityForResult(intent,NOTESEDITORUPDATE);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            //selectedNote = new Notes();
            selectedNote = notes;
            selectedNote.setPinned(!selectedNote.isPinned());
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
          Log.d(TAG, "onMenuItemClick: isPinned : "+selectedNote.isPinned());
                if (selectedNote.isPinned()) {
                    database.mainDAObj().pin(selectedNote.getID(), false);
                    Log.d(TAG, "onMenuItemClick: ispinned reached if block");
                    Toast.makeText(MainActivity.this, "unpinned!", Toast.LENGTH_SHORT).show();
                } else {
                    database.mainDAObj().pin(selectedNote.getID(), true);
                    Log.d(TAG, "onMenuItemClick: ispinned reached else block");
                    Toast.makeText(MainActivity.this, "pinned!", Toast.LENGTH_SHORT).show();
                }
                notesList.clear();
                notesList.addAll(database.mainDAObj().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
      }
      else {
          database.mainDAObj().delete(selectedNote);
          Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
          notesList.remove(selectedNote);
          notesListAdapter.notifyDataSetChanged();
          return true;
      }
    }
}