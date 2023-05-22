package models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Anime anime);
    }
    private List<Anime> animeList;
    private OnItemClickListener itemClickListener;

    public AnimeAdapter(List<Anime> animeList) {
        this.animeList = animeList;
    }

    public void setAnimeList(List<Anime> animeList){
        this.animeList = animeList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new ViewHolder(view, itemClickListener, animeList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        // Bind data to the views in the item layout
        Glide.with(holder.itemView)
                .load(anime.getImage().getOriginal())
                .into(holder.pieceImage);
        holder.pieceRussianName.setText(anime.getRussian());
        holder.pieceEnglishName.setText(anime.getName());
        holder.pieceKind.setText(anime.getKind());
        holder.pieceStatus.setText(anime.getStatus());
        holder.pieceScore.setText(anime.getScore());

        // Set click listener for the button
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && itemClickListener != null) {
                    Anime clickedAnime = animeList.get(adapterPosition);
                    itemClickListener.onItemClick(clickedAnime);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pieceImage;
        Spinner userListSpinner;
        TextView pieceRussianName;
        TextView pieceEnglishName;
        TextView pieceKind;
        TextView pieceStatus;
        TextView pieceScore;
        Button detailsButton;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener, List<Anime> animeList) {
            super(itemView);
            pieceImage = itemView.findViewById(R.id.pieceImage);
            userListSpinner = itemView.findViewById(R.id.userListSpinner);
            pieceRussianName = itemView.findViewById(R.id.pieceRussianName);
            pieceEnglishName = itemView.findViewById(R.id.pieceEnglishName);
            pieceKind = itemView.findViewById(R.id.pieceKind);
            pieceStatus = itemView.findViewById(R.id.pieceStatus);
            pieceScore = itemView.findViewById(R.id.pieceScore);
            detailsButton = itemView.findViewById(R.id.detailsButton);

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Anime anime = animeList.get(position);
                        listener.onItemClick(anime);
                    }
                }
            });
        }
    }
}
