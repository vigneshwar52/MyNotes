package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Notes;
import com.example.myapplication.NotesOnClickListener;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesOnClickListener listener;

    public NotesListAdapter(Context context, List<Notes> list, NotesOnClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvTitle.setSelected(true);

        holder.tvDes.setText(list.get(position).getDescription());

        holder.tvDate.setText(list.get(position).getDate());
        holder.tvDate.setSelected(true);

        if (list.get(position).isPinned()) {
            holder.imgView_pin.setImageResource(R.drawable.pin);
        }
        else{
            holder.imgView_pin.setImageResource(0);
        }

        holder.notesContainer.setCardBackgroundColor(list.get(position).getColor());

        holder.notesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        //onLong Click ---
        holder.notesContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notesContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Notes> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    CardView notesContainer;
    TextView tvTitle, tvDes, tvDate;
    ImageView fab_btn,imgView_pin;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notesContainer = itemView.findViewById(R.id.notes_container);
        tvTitle = itemView.findViewById(R.id.txtView_title);
        tvDes = itemView.findViewById(R.id.txtView_desc);
        tvDate = itemView.findViewById(R.id.txtView_date);
        fab_btn = itemView.findViewById(R.id.fab_btn);
        imgView_pin = itemView.findViewById(R.id.imgView_pin);
    }
}