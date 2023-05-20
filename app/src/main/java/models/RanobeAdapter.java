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

public class RanobeAdapter extends RecyclerView.Adapter<RanobeAdapter.ViewHolder> {
    private List<Manga> pieceList;

    public RanobeAdapter(List<Manga> pieceList) {
        this.pieceList = pieceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manga ranobe = pieceList.get(position);

        // Bind data to the views in the item layout
        Glide.with(holder.itemView)
                .load(ranobe.getImage().getOriginal())
                .into(holder.pieceImage);
        holder.pieceRussianName.setText(ranobe.getRussian());
        holder.pieceEnglishName.setText(ranobe.getName());
        holder.pieceKind.setText(ranobe.getKind());
        holder.pieceStatus.setText(ranobe.getStatus());
        holder.pieceScore.setText(ranobe.getScore());

        // Set click listener for the button
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pieceList.size();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pieceImage = itemView.findViewById(R.id.pieceImage);
            userListSpinner = itemView.findViewById(R.id.userListSpinner);
            pieceRussianName = itemView.findViewById(R.id.pieceRussianName);
            pieceEnglishName = itemView.findViewById(R.id.pieceEnglishName);
            pieceKind = itemView.findViewById(R.id.pieceKind);
            pieceStatus = itemView.findViewById(R.id.pieceStatus);
            pieceScore = itemView.findViewById(R.id.pieceScore);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }
}