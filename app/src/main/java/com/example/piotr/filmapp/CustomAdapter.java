package com.example.piotr.filmapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<FilmModel> films = new ArrayList<>();

    private ClickListener clickListener, removeListener;

    public CustomAdapter() {
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setRemoveListener(ClickListener removeListener) {
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.film_item, parent, false);

        // Return a new holder instance
        CustomViewHolder viewHolder = new CustomViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        final FilmModel filmModel = films.get(position);

        holder.title.setText(filmModel.getTitle());
        holder.year.setText(filmModel.getYear());

        Picasso.get().load(filmModel.getPoster()).error(R.drawable.ic_photo).into(holder.image);

        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeListener != null) {
                    removeListener.clickItem(filmModel);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.clickItem(filmModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public void setData(List<FilmModel> films) {
        this.films = films;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView year;
        ImageButton delete_button;

        public CustomViewHolder(final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            year = itemView.findViewById(R.id.year);
            delete_button = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface ClickListener {
        void clickItem(FilmModel item);
    }
}

