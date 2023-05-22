package com.example.mobileversion.ui.piece.mangaID;

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
import com.example.mobileversion.ui.piece.animeID.AnimeIDActivity;
import com.example.mobileversion.ui.piece.animeID.AnimeIDViewModel;

import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.AnimeID;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.MangaID;
import models.Publisher;
import models.Studio;

public class MangaIDActivity extends AppCompatActivity {
    private AnimeAdapter animeAdapter;
    private MangaAdapter mangaAdapter;

    private long id = 1;
    private ImageView Image;
    private TextView RussianTextView;
    private TextView EnglishTextView;
    private TextView KindTextView;
    private TextView StatusTextView;
    private TextView ChapterCountTextView;
    private TextView PublishersTextView;
    private TextView GenresTextView;
    private RecyclerView relatedAnimeRecyclerView;
    private RecyclerView relatedMangaRecyclerView;
    private RecyclerView relatedRanobeRecyclerView;
    private RecyclerView SimilarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_id);
        MangaIDViewModel mangaIDViewModel =
                new ViewModelProvider(this).get(MangaIDViewModel.class);

        relatedAnimeRecyclerView = findViewById(R.id.relatedAnimeRecyclerView);
        relatedAnimeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedMangaRecyclerView = findViewById(R.id.relatedMangaRecyclerView);
        relatedMangaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRanobeRecyclerView = findViewById(R.id.relatedRanobeRecyclerView);
        relatedRanobeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SimilarRecyclerView = findViewById(R.id.SimilarRecyclerView);
        SimilarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        GenresTextView = findViewById(R.id.GenresTextView);
        PublishersTextView = findViewById(R.id.PublishersTextView);
        Image = findViewById(R.id.Image);
        RussianTextView = findViewById(R.id.RussianTextView);
        EnglishTextView = findViewById(R.id.EnglishTextView);
        KindTextView = findViewById(R.id.KindTextView);
        StatusTextView = findViewById(R.id.StatusTextView);
        ChapterCountTextView = findViewById(R.id.ChapterCountTextView);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getLongExtra("Id", 0);
            mangaIDViewModel.getManga((int) id);
            mangaIDViewModel.getMangaLiveData().observe(this, new Observer<MangaID>() {
                @Override
                public void onChanged(MangaID mangaID) {
                    Glide.with(getApplicationContext()).load(mangaIDViewModel.mangaID.getImage().getOriginal()).into(Image);

                    StringBuilder publishers = new StringBuilder();
                    for (Publisher publisher : mangaIDViewModel.mangaID.getPublishers()) {
                        publishers.append(String.format(publisher.getName() + "; "));
                    }
                    PublishersTextView.setText(publishers);

                    StringBuilder genres = new StringBuilder();
                    for (Genre genre : mangaIDViewModel.mangaID.getGenres()) {
                        genres.append(String.format(genre.getRussian() + "; "));
                    }
                    GenresTextView.setText(genres);

                    RussianTextView.setText(mangaIDViewModel.mangaID.getRussian());
                    EnglishTextView.setText(mangaIDViewModel.mangaID.getName());
                    KindTextView.setText(mangaIDViewModel.mangaID.getKind());
                    StatusTextView.setText(mangaIDViewModel.mangaID.getStatus());
                    ChapterCountTextView.setText(String.format("%d", mangaIDViewModel.mangaID.getChapters()));
                }
            });

            mangaIDViewModel.getrelatedAnimeLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData);

                    animeAdapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Anime anime) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.this,
                                    com.example.mobileversion.ui.piece.animeID.AnimeIDActivity.class);

                            intent.putExtra("Id", anime.getId());
                            startActivity(intent);
                        }
                    });

                    relatedAnimeRecyclerView.setAdapter(animeAdapter);
                }
            });
            mangaIDViewModel.getrelatedMangaLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.this,
                                    com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.class);

                            intent.putExtra("Id", manga.getId());
                            startActivity(intent);
                        }
                    });

                    relatedMangaRecyclerView.setAdapter(mangaAdapter);
                }
            });
            mangaIDViewModel.getrelatedRanobeLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga ranobe) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.this,
                                    com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.class);

                            intent.putExtra("Id", ranobe.getId());
                            startActivity(intent);
                        }
                    });

                    relatedRanobeRecyclerView.setAdapter(mangaAdapter);
                }
            });
            mangaIDViewModel.getsimilarLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            Intent intent = new Intent(MangaIDActivity.this, MangaIDActivity.class);

                            intent.putExtra("Id", manga.getId());
                            startActivity(intent);
                        }
                    });

                    SimilarRecyclerView.setAdapter(mangaAdapter);
                }
            });
        }
    }
}