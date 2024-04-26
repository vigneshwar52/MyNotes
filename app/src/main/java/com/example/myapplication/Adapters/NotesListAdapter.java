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

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder>{
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
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvTitle.setSelected(true);

        holder.tvDes.setText(list.get(position).getDescription());

        holder.tvDate.setText(list.get(position).getDate());
        holder.tvDate.setSelected(true);

        if(list.get(position).isPinned()){
            holder.fab_btn.setImageResource(R.drawable.pin);
        }
//        else{
//            holder.fab_btn.setImageResource(0);
//        }

        int colorCode = getRandomColor();
        holder.notesContainer.setCardBackgroundColor(holder.itemView.getResources().getColor(colorCode,null));
        holder.notesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });
        holder.notesContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(list.get(holder.getAdapterPosition()),holder.notesContainer);
                return true;
            }
        });
    }
    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.lightgreen);
        colorCode.add(R.color.lightgrey);
        colorCode.add(R.color.violet);
        colorCode.add(R.color.blue);
        colorCode.add(R.color.green);
        colorCode.add(R.color.grey);

        Random random = new Random();
        int randomColor = random.nextInt(colorCode.size());
        return colorCode.get(randomColor);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class NotesViewHolder extends RecyclerView.ViewHolder{
    CardView notesContainer;
    TextView tvTitle,tvDes,tvDate;
    ImageView fab_btn;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notesContainer = itemView.findViewById(R.id.notes_container);
        tvTitle = itemView.findViewById(R.id.txtView_title);
        tvDes = itemView.findViewById(R.id.txtView_desc);
        tvDate = itemView.findViewById(R.id.txtView_date);
        fab_btn = itemView.findViewById(R.id.fab_btn);
    }
}