package com.example.mobileversion.ui.piece.animeID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mobileversion.R;

import models.AnimeID;

public class AnimeIDActivity extends AppCompatActivity {
    private long id = 1;
    private TextView animeRussianTextView;
    private TextView animeEnglishTextView;
    private TextView animeRatingTextView;
    private TextView animeKindTextView;
    private TextView animeStatusTextView;
    private TextView animeSeriesCountTextView;
    private TextView animeDurabilityTextView;

    private RecyclerView animeStudiosRecyclerView;
    private RecyclerView animeGenresRecyclerView;
    private RecyclerView animeScreensRecyclerView;
    private RecyclerView animeVideosRecyclerView;
    private RecyclerView relatedAnimeRecyclerView;
    private RecyclerView relatedMangaRecyclerView;
    private RecyclerView relatedRanobeRecyclerView;
    private RecyclerView animeSimilarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_id);

        AnimeIDViewModel animeIDViewModel =
                new ViewModelProvider(this).get(AnimeIDViewModel.class);

        animeStudiosRecyclerView = findViewById(R.id.animeStudiosRecyclerView);
        animeStudiosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animeGenresRecyclerView = findViewById(R.id.animeGenresRecyclerView);
        animeGenresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animeScreensRecyclerView = findViewById(R.id.animeScreensRecyclerView);
        animeScreensRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animeVideosRecyclerView = findViewById(R.id.animeVideosRecyclerView);
        animeVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        relatedAnimeRecyclerView = findViewById(R.id.relatedAnimeRecyclerView);
        relatedAnimeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        relatedMangaRecyclerView = findViewById(R.id.relatedMangaRecyclerView);
        relatedMangaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        relatedRanobeRecyclerView = findViewById(R.id.relatedRanobeRecyclerView);
        relatedRanobeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animeSimilarRecyclerView = findViewById(R.id.animeSimilarRecyclerView);
        animeSimilarRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        animeRussianTextView = findViewById(R.id.animeRussianTextView);
        animeEnglishTextView = findViewById(R.id.animeEnglishTextView);
        animeRatingTextView = findViewById(R.id.animeRatingTextView);
        animeKindTextView = findViewById(R.id.animeKindTextView);
        animeStatusTextView = findViewById(R.id.animeStatusTextView);
        animeSeriesCountTextView = findViewById(R.id.animeSeriesCountTextView);
        animeDurabilityTextView = findViewById(R.id.animeDurabilityTextView);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getLongExtra("AnimeId", 0);
            animeIDViewModel.getAnime((int) id);
            animeIDViewModel.getAnimeLiveData().observe(this, new Observer<AnimeID>() {
                @Override
                public void onChanged(AnimeID animeID) {
                    animeRussianTextView.setText(animeIDViewModel.animeID.getRussian());
                    animeEnglishTextView.setText(animeIDViewModel.animeID.getName());
                    animeRatingTextView.setText(animeIDViewModel.animeID.getRating());
                    animeKindTextView.setText(animeIDViewModel.animeID.getKind());
                    animeStatusTextView.setText(animeIDViewModel.animeID.getStatus());
                    animeSeriesCountTextView.setText(String.format("%d", animeIDViewModel.animeID.getEpisodes()));
                    animeDurabilityTextView.setText(String.format("%d", animeIDViewModel.animeID.getDuration()));
                }
            });
        }
    }
}