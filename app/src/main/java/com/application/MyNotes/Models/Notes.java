package com.application.MyNotes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "notes")
public class Notes implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int ID = 0;

    @ColumnInfo()
    String title = "";

    String description = "";

    String date = "";

    boolean pinned;
    private int color;

    public Notes() {

   }
    public Notes(int ID, String title, String description, String date, boolean pinned, int color) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.pinned = pinned;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
