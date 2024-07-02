package com.example.movieappproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieappproject.Movie;
import com.example.movieappproject.R;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {

    private Context context;
    private List<Movie> favoriteMoviesList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }
    public FavoriteMoviesAdapter(Context context, List<Movie> favoriteMoviesList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.favoriteMoviesList = favoriteMoviesList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = favoriteMoviesList.get(position);

        holder.movieNameTxt.setText(movie.getTitle());
        holder.movieRateTxt.setText(movie.getRating());
        holder.movieDateTxt.setText(movie.getYear());
        Glide.with(context).load(movie.getLarge_cover_image()).into(holder.posterImageView);

        holder.deleteImageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onDeleteClick(adapterPosition);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return favoriteMoviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImageView, deleteImageView;
        TextView movieNameTxt, movieRateTxt, movieDateTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            posterImageView = itemView.findViewById(R.id.imageView);
            movieNameTxt = itemView.findViewById(R.id.title);
            movieRateTxt = itemView.findViewById(R.id.rating);
            movieDateTxt = itemView.findViewById(R.id.year);
            deleteImageView = itemView.findViewById(R.id.imageView9);
        }
    }
}