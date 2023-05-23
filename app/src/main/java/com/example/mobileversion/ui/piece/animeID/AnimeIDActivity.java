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
import com.example.mobileversion.ui.piece.mangaID.MangaIDActivity;
import com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity;

import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.AnimeID;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.RanobeAdapter;
import models.Studio;

public class AnimeIDActivity extends AppCompatActivity {
    private AnimeAdapter animeAdapter;
    private MangaAdapter mangaAdapter;
    private RanobeAdapter ranobeAdapter;

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
    private RecyclerView SimilarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_id);
        AnimeIDViewModel animeIDViewModel =
                new ViewModelProvider(this).get(AnimeIDViewModel.class);

        animeScreensRecyclerView = findViewById(R.id.animeScreensRecyclerView);
        animeScreensRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        animeVideosRecyclerView = findViewById(R.id.animeVideosRecyclerView);
        animeVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedAnimeRecyclerView = findViewById(R.id.relatedAnimeRecyclerView);
        relatedAnimeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedMangaRecyclerView = findViewById(R.id.relatedMangaRecyclerView);
        relatedMangaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRanobeRecyclerView = findViewById(R.id.relatedRanobeRecyclerView);
        relatedRanobeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SimilarRecyclerView = findViewById(R.id.SimilarRecyclerView);
        SimilarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
            id = intent.getLongExtra("Id", 0);
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

            animeIDViewModel.getrelatedAnimeLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData);

                    animeAdapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Anime anime) {
                            // Обработайте нажатие кнопки для выбранного аниме
                            Intent intent = new Intent(AnimeIDActivity.this, AnimeIDActivity.class);

                            intent.putExtra("Id", anime.getId());
                            startActivity(intent);
                        }
                    });

                    relatedAnimeRecyclerView.setAdapter(animeAdapter);
                }
            });
            animeIDViewModel.getrelatedMangaLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            // Обработайте нажатие кнопки для выбранного аниме
                            Intent intent = new Intent(AnimeIDActivity.this, MangaIDActivity.class);

                            intent.putExtra("Id", manga.getId());
                            startActivity(intent);
                        }
                    });

                    relatedMangaRecyclerView.setAdapter(mangaAdapter);
                }
            });
            animeIDViewModel.getrelatedRanobeLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    ranobeAdapter = new RanobeAdapter(mangaListData);

                    ranobeAdapter.setOnItemClickListener(new RanobeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga ranobe) {
                            // Обработайте нажатие кнопки для выбранного аниме
                            Intent intent = new Intent(AnimeIDActivity.this, RanobeIDActivity.class);

                            intent.putExtra("Id", ranobe.getId());
                            startActivity(intent);
                        }
                    });

                    relatedRanobeRecyclerView.setAdapter(ranobeAdapter);
                }
            });
            animeIDViewModel.getsimilarLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData);

                    animeAdapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Anime anime) {
                            Intent intent = new Intent(AnimeIDActivity.this, AnimeIDActivity.class);

                            intent.putExtra("Id", anime.getId());
                            startActivity(intent);
                        }
                    });

                    SimilarRecyclerView.setAdapter(animeAdapter);
                }
            });
        }
    }
}