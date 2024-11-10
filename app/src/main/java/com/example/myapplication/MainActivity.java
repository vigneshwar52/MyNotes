package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.myapplication.Adapters.NotesListAdapter;
import com.example.myapplication.Database.RoomDB;
import com.example.myapplication.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private static final int NOTES_NEW = 0x01;
    private static final int NOTES_UPDATE = 0x02;
    SharedPreferences sharedPrefs;String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        getViews();
        sharedPrefs = getSharedPreferences("user_prefs",MODE_PRIVATE);
        userType = sharedPrefs.getString("user_type", "local");

        if (userType.equals("remote")) {
            loadNotesFromFirebase();
        } else {
            loadNotesFromRoom();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotesEditorActivity.class);
                startActivityForResult(intent,NOTES_NEW);
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

    private void loadNotesFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid()).collection("notes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            notesList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Notes note = document.toObject(Notes.class);
                                notesList.add(note);
                            }
                            updateRecycler(notesList);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void loadNotesFromRoom() {
        database = RoomDB.getInstance(this);
        notesList = database.mainDAObj().getAll();
        updateRecycler(notesList);
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
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Get the updated or new note from NotesEditorActivity
            Notes note = (Notes) data.getSerializableExtra("notes");
            if (note == null) return;
            if (requestCode == NOTES_NEW) {
                if (userType.equals("local")) {
                    saveNoteToRoom(note);
                } else if (userType.equals("firebase")) {
                    saveNoteToFirebase(note);
                }
            } else if (requestCode == NOTES_UPDATE) {
                if (userType.equals("local")) {
                    updateNoteToRoom(note);
                } else if (userType.equals("firebase")) {
                    updateNoteToFirebase(note);
                }
            }
        }
        notesListAdapter.notifyDataSetChanged();
    }

    private void updateNoteToFirebase(Notes note) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null && note.getFirebaseID()!=null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(currentUser.getUid()).collection("notes")
                    .document(note.getFirebaseID())
                    .set(note)
                    .addOnSuccessListener(aVoid -> {
                        int index = getNoteIndexByFirebaseId(note.getFirebaseID());
                        if (index != -1) {
                            notesList.set(index, note);
                            notesListAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(this, "Note updated in Firebase", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating note in Firebase", e));
        }
    }

    private int getNoteIndexByFirebaseId(String firebaseID) {
        for (int i = 0; i < notesList.size(); i++) {
            if (firebaseID.equals(notesList.get(i).getFirebaseID())) {
                return i;
            }
        }
        return -1;
    }

    private void saveNoteToFirebase(Notes note) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid()).collection("notes")
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        note.setFirebaseID(documentReference.getId());
                        notesList.add(note);
                        notesListAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Note saved to Firebase", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e-> Log.e(TAG, "Error adding note to Firebase", e));
        }
    }

    private void saveNoteToRoom(Notes note) {
        database.mainDAObj().insert(note);
        //notesList.clear();
        //notesList.addAll(database.mainDAObj().getAll());
        notesList.add(note);
        Toast.makeText(MainActivity.this, "Note Created Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateNoteToRoom(Notes note) {
        database.mainDAObj().update(note.getID(),note.getTitle(),note.getDescription());
        notesList.clear();
        notesList.addAll(database.mainDAObj().getAll());
        notesListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Note updated locally", Toast.LENGTH_SHORT).show();    }

    private void updateRecycler(List<Notes> notesList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(this,notesList,notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
        Toast.makeText(this, "Note saved locally", Toast.LENGTH_SHORT).show();
    }

    private  final NotesOnClickListener notesClickListener = new NotesOnClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this,NotesEditorActivity.class);
            intent.putExtra("edit_notes",notes);
            startActivityForResult(intent,NOTES_UPDATE);
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