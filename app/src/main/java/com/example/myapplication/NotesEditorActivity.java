package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesEditorActivity extends AppCompatActivity {

    EditText editTextTitle,editTextDesc;
    ImageView imageViewSave,imageViewBack;
    Notes notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_editor);
        Log.d("lifecycle","onCreate invoked");

        imageViewSave = findViewById(R.id.imgView_save);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDesc = findViewById(R.id.editTextDesc);
        imageViewBack = findViewById(R.id.imgView_back);
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String desc = editTextDesc.getText().toString().trim();

                if(desc.isEmpty()){
                    Toast.makeText(NotesEditorActivity.this,"please add some notes to save",Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE dd MM yyyy HH:mm:ss a");
                Date date = new Date();

                notes = new Notes();

                notes.setTitle(title);
                notes.setDescription(desc);
                notes.setDate(formatter.format(date));

                //notes.setDate(String.valueOf(date));

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