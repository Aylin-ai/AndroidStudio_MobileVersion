package com.example.mobileversion.ui.piece.animeID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;

import models.AnimeID;
import models.Genre;
import models.Studio;

public class AnimeIDActivity extends AppCompatActivity {
    private long id = 1;
    private ImageView animeImage;
    private TextView animeRussianTextView;
    private TextView animeEnglishTextView;
    private TextView animeRatingTextView;
    private TextView animeKindTextView;
    private TextView animeStatusTextView;
    private TextView animeSeriesCountTextView;
    private TextView animeDurabilityTextView;
    private TextView animeStudiosTextView;
    private TextView animeGenresTextView;
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

        animeGenresTextView = findViewById(R.id.animeGenresTextView);
        animeStudiosTextView = findViewById(R.id.animeStudiosTextView);
        animeImage = findViewById(R.id.animeImage);
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
                    Glide.with(getApplicationContext()).load(animeIDViewModel.animeID.getImage().getOriginal()).into(animeImage);

                    StringBuilder studios = new StringBuilder();
                    for (Studio studio : animeIDViewModel.animeID.getStudios()) {
                        studios.append(String.format(studio.getName() + "; "));
                    }
                    animeStudiosTextView.setText(studios);

                    StringBuilder genres = new StringBuilder();
                    for (Genre genre : animeIDViewModel.animeID.getGenres()) {
                        genres.append(String.format(genre.getRussian() + "; "));
                    }
                    animeGenresTextView.setText(genres);

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