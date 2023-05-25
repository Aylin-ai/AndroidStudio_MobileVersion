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

public class ScreenAdapter extends RecyclerView.Adapter<ScreenAdapter.ViewHolder> {

    private List<Screenshots> screenshotsList;

    public ScreenAdapter(List<Screenshots> screenshotsList) {
        this.screenshotsList = screenshotsList;
    }

    public void setScreenshotsList(List<Screenshots> screenshotsList){
        this.screenshotsList = screenshotsList;
    }

    @NonNull
    @Override
    public ScreenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screen, parent, false);
        return new ScreenAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenAdapter.ViewHolder holder, int position) {
        try {
            Screenshots screen = screenshotsList.get(position);

            // Bind data to the views in the item layout
            Glide.with(holder.ScreenImageView.getContext())
                    .load(screen.getOriginal())
                    .into(holder.ScreenImageView);
        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        return screenshotsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ScreenImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ScreenImageView = itemView.findViewById(R.id.ScreenImageView);
        }
    }

}
