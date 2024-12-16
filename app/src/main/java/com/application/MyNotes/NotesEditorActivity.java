package com.application.MyNotes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.application.MyNotes.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class NotesEditorActivity extends AppCompatActivity {
    EditText editTextTitle,editTextDesc;
    ImageView imageViewSave,imageViewBack;
    Notes notes;
    Boolean isOldNote = false;
    boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_editor);
        Log.d("lifecycle","onCreate invoked");

        getViews();

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_data");
            editTextTitle.setText(notes.getTitle());
            editTextDesc.setText(notes.getDescription());
            isOldNote = true;
        }catch (Exception e){
            Log.e("Exception in Notes Editor = ", Objects.requireNonNull(e.getMessage()));
        }
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String desc = editTextDesc.getText().toString().trim();

                if(desc.isEmpty()){
                    Toast.makeText(NotesEditorActivity.this,"please add some Description \n to save this note",Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss a");
                Date date = new Date();

                Random random = new Random();
                int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

                if(!isOldNote){
                    notes = new Notes();
                }
                notes.setTitle(title);
                notes.setDescription(desc);
                notes.setDate(formatter.format(date));
                notes.setColor(color);

                saveNotesToFirebase(notes);

                Intent intent = new Intent();
                intent.putExtra("notes",notes);

                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getViews() {
        imageViewSave = findViewById(R.id.imgView_save);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDesc = findViewById(R.id.editTextDesc);
        imageViewBack = findViewById(R.id.imgView_back);
    }

    private void saveNotesToFirebase(Notes notes) {

    }

    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onStart invoked");
    }
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle","onStop invoked");
    }
    protected void onResume() {

        super.onResume();
        Log.d("lifecycle","onResume invoked");
    }
    protected void onRestart() {

        super.onRestart();
        Log.d("lifecycle","onRestart invoked");
    }
    protected void onDestroy() {

        super.onDestroy();Log.d("lifecycle","onDestroy invoked");
    }
}