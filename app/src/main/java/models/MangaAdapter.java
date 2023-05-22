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

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Manga manga);
    }
    private List<Manga> mangaList;
    private MangaAdapter.OnItemClickListener itemClickListener;

    public MangaAdapter(List<Manga> animeList) {
        this.mangaList = animeList;
    }

    public void setMangaList(List<Manga> mangaList){
        this.mangaList = mangaList;
    }

    public void setOnItemClickListener(MangaAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new ViewHolder(view, itemClickListener, mangaList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manga manga = mangaList.get(position);

        // Bind data to the views in the item layout
        Glide.with(holder.itemView)
                .load(manga.getImage().getOriginal())
                .into(holder.pieceImage);
        holder.pieceRussianName.setText(manga.getRussian());
        holder.pieceEnglishName.setText(manga.getName());
        holder.pieceKind.setText(manga.getKind());
        holder.pieceStatus.setText(manga.getStatus());
        holder.pieceScore.setText(manga.getScore());

        // Set click listener for the button
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && itemClickListener != null) {
                    Manga clickedManga = mangaList.get(adapterPosition);
                    itemClickListener.onItemClick(clickedManga);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
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

        public ViewHolder(@NonNull View itemView, MangaAdapter.OnItemClickListener listener, List<Manga> mangaList) {
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
                        Manga manga = mangaList.get(position);
                        listener.onItemClick(manga);
                    }
                }
            });
        }
    }
}