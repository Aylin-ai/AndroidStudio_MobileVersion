package models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Video video);
    }
    private List<Video> videoList;
    private VideoAdapter.OnItemClickListener itemClickListener;

    public VideoAdapter(List<Video> videoList) {
        this.videoList = videoList;
    }

    public void setVideoList(List<Video> videoList){
        this.videoList = videoList;
    }

    public void setOnItemClickListener(VideoAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoAdapter.ViewHolder(view, itemClickListener, videoList);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        try {
            Video video = videoList.get(position);
            holder.LinkTextView.setText(video.getName());
            holder.LinkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && itemClickListener != null) {
                        Video clickedVideo = videoList.get(adapterPosition);
                        itemClickListener.onItemClick(clickedVideo);
                    }
                }
            });

        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView LinkTextView;

        public ViewHolder(@NonNull View itemView, VideoAdapter.OnItemClickListener listener, List<Video> videoList) {
            super(itemView);
            LinkTextView = itemView.findViewById(R.id.LinkTextView);
            LinkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Video video = videoList.get(position);
                        listener.onItemClick(video);
                    }
                }
            });
        }
    }

}
