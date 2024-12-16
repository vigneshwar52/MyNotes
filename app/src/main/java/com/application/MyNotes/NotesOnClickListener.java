package com.application.MyNotes;

import androidx.cardview.widget.CardView;

import com.application.MyNotes.Models.Notes;

public interface NotesOnClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
