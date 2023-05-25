package com.example.mobileversion.ui.piece.ranobeID;

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
import com.example.mobileversion.ui.piece.mangaID.MangaIDViewModel;

import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.MangaID;
import models.Publisher;
import models.RanobeAdapter;

public class RanobeIDActivity extends AppCompatActivity {
    private AnimeAdapter animeAdapter;
    private MangaAdapter mangaAdapter;
    private RanobeAdapter ranobeAdapter;

    private long id = 1;
    private ImageView Image;
    private TextView RussianTextView;
    private TextView EnglishTextView;
    private TextView KindTextView;
    private TextView StatusTextView;
    private TextView ChapterCountTextView;
    private TextView PublishersTextView;
    private TextView GenresTextView;
    private TextView DescTextView;
    private RecyclerView relatedAnimeRecyclerView;
    private RecyclerView relatedMangaRecyclerView;
    private RecyclerView relatedRanobeRecyclerView;
    private RecyclerView SimilarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_id);
        RanobeIDViewModel ranobeIDViewModel =
                new ViewModelProvider(this).get(RanobeIDViewModel.class);

        relatedAnimeRecyclerView = findViewById(R.id.relatedAnimeRecyclerView);
        relatedAnimeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedMangaRecyclerView = findViewById(R.id.relatedMangaRecyclerView);
        relatedMangaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRanobeRecyclerView = findViewById(R.id.relatedRanobeRecyclerView);
        relatedRanobeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SimilarRecyclerView = findViewById(R.id.SimilarRecyclerView);
        SimilarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DescTextView = findViewById(R.id.DescTextView);
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
            ranobeIDViewModel.getRanobe((int) id);
            ranobeIDViewModel.getRanobeLiveData().observe(this, new Observer<MangaID>() {
                @Override
                public void onChanged(MangaID mangaID) {
                    Glide.with(getApplicationContext()).load(ranobeIDViewModel.ranobeID.getImage().getOriginal()).into(Image);

                    StringBuilder publishers = new StringBuilder();
                    for (Publisher publisher : ranobeIDViewModel.ranobeID.getPublishers()) {
                        publishers.append(String.format(publisher.getName() + "; "));
                    }
                    PublishersTextView.setText(publishers);

                    StringBuilder genres = new StringBuilder();
                    for (Genre genre : ranobeIDViewModel.ranobeID.getGenres()) {
                        genres.append(String.format(genre.getRussian() + "; "));
                    }
                    GenresTextView.setText(genres);

                    RussianTextView.setText(ranobeIDViewModel.ranobeID.getRussian());
                    EnglishTextView.setText(ranobeIDViewModel.ranobeID.getName());
                    KindTextView.setText(ranobeIDViewModel.ranobeID.getKind());
                    StatusTextView.setText(ranobeIDViewModel.ranobeID.getStatus());
                    ChapterCountTextView.setText(String.format("%d", ranobeIDViewModel.ranobeID.getChapters()));
                    DescTextView.setText(ranobeIDViewModel.ranobeID.getDescription());
                }
            });

            ranobeIDViewModel.getrelatedAnimeLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData);

                    animeAdapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Anime anime) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.this,
                                    com.example.mobileversion.ui.piece.animeID.AnimeIDActivity.class);

                            intent.putExtra("Id", anime.getId());
                            startActivity(intent);
                        }
                    });

                    relatedAnimeRecyclerView.setAdapter(animeAdapter);
                }
            });
            ranobeIDViewModel.getrelatedMangaLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.this,
                                    com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.class);

                            intent.putExtra("Id", manga.getId());
                            startActivity(intent);
                        }
                    });

                    relatedMangaRecyclerView.setAdapter(mangaAdapter);
                }
            });
            ranobeIDViewModel.getrelatedRanobeLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    ranobeAdapter = new RanobeAdapter(mangaListData);

                    ranobeAdapter.setOnItemClickListener(new RanobeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga ranobe) {
                            // Обработайте нажатие кнопки для выбранного аниме
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.this,
                                    com.example.mobileversion.ui.piece.mangaID.MangaIDActivity.class);

                            intent.putExtra("Id", ranobe.getId());
                            startActivity(intent);
                        }
                    });

                    relatedRanobeRecyclerView.setAdapter(ranobeAdapter);
                }
            });
            ranobeIDViewModel.getsimilarLiveData().observe(this, new Observer<List<Manga>>() {
                @Override
                public void onChanged(List<Manga> mangaListData) {
                    // Обновляем адаптер с новыми данными
                    mangaAdapter = new MangaAdapter(mangaListData);

                    mangaAdapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.this,
                                    com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.class);

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