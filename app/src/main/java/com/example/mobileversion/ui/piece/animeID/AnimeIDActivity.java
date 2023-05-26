package com.example.mobileversion.ui.piece.animeID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;
import com.example.mobileversion.ui.piece.mangaID.MangaIDActivity;
import com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.AnimeID;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.PieceInUserList;
import models.PieceRepository;
import models.RanobeAdapter;
import models.ScreenAdapter;
import models.Screenshots;
import models.Studio;
import models.Video;
import models.VideoAdapter;

public class AnimeIDActivity extends AppCompatActivity {
    private AnimeAdapter animeAdapter;
    private MangaAdapter mangaAdapter;
    private RanobeAdapter ranobeAdapter;
    private ScreenAdapter screenAdapter;
    private VideoAdapter videoAdapter;
    private long id = 1;
    private ImageView animeImage;
    private Spinner pieceSpinner;
    private TextView animeRussianTextView;
    private TextView animeEnglishTextView;
    private TextView animeRatingTextView;
    private TextView animeKindTextView;
    private TextView animeStatusTextView;
    private TextView animeSeriesCountTextView;
    private TextView animeDurabilityTextView;
    private TextView animeStudiosTextView;
    private TextView animeGenresTextView;
    private TextView DescTextView;
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

        DescTextView = findViewById(R.id.DescTextView);
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
        pieceSpinner = findViewById(R.id.pieceSpinner);
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
                    DescTextView.setText(animeIDViewModel.animeID.getDescription());

                    videoAdapter = new VideoAdapter(Arrays.asList(animeID.getVideos()));

                    videoAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Video video) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(video.getUrl());
                            startActivity(intent);
                        }
                    });

                    animeVideosRecyclerView.setAdapter(videoAdapter);

                    loadAnimeFromDatabase(id, new PieceRepository.PieceDataLoadCallback() {
                        @Override
                        public void onDataLoaded(PieceInUserList piece) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    AnimeIDActivity.this,
                                    R.array.spinner_anime_list,
                                    android.R.layout.simple_spinner_item
                            );
                            if (piece != null) {
                                int positionInSpinner = adapter.getPosition(piece.getUserList());
                                pieceSpinner.setSelection(positionInSpinner);
                            } else {
                                pieceSpinner.setSelection(adapter.getPosition("Нет"));
                            }
                        }
                    });
                    pieceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedValue = parent.getItemAtPosition(position).toString();
                            if (selectedValue.equals("Нет")) {
                                deletePieceFromDatabase(animeIDViewModel.animeID);
                            } else {
                                loadAnimeFromDatabase(id, new PieceRepository.PieceDataLoadCallback() {
                                    @Override
                                    public void onDataLoaded(PieceInUserList piece) {
                                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                                AnimeIDActivity.this,
                                                R.array.spinner_anime_list,
                                                android.R.layout.simple_spinner_item
                                        );
                                        if (piece != null) {
                                            updatePieceInDatabase(piece, selectedValue);
                                        } else {
                                            addPieceToDatabase(animeIDViewModel.animeID, selectedValue);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Обработка ситуации, когда не выбран ни один элемент
                        }
                    });
                }
            });

            animeIDViewModel.getrelatedAnimeLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData, getApplicationContext());

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
                    mangaAdapter = new MangaAdapter(mangaListData, getApplicationContext());

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
                    ranobeAdapter = new RanobeAdapter(mangaListData, getApplicationContext());

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
                    animeAdapter = new AnimeAdapter(animeListData, getApplicationContext());

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
            animeIDViewModel.getScreensLiveData().observe(this, new Observer<List<Screenshots>>() {
                @Override
                public void onChanged(List<Screenshots> screenshotsList) {
                    screenAdapter = new ScreenAdapter(screenshotsList);
                    animeScreensRecyclerView.setAdapter(screenAdapter);
                }
            });
        }
    }
    public void loadAnimeFromDatabase(long id, PieceRepository.PieceDataLoadCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("anime").child(String.valueOf(id));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PieceInUserList piece = snapshot.getValue(PieceInUserList.class);
                callback.onDataLoaded(piece);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    private void deletePieceFromDatabase(AnimeID anime) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference animeRef = database.getReference("anime").child(String.valueOf(anime.getId()));
        animeRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Успешно удалено из базы данных
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Обработка ошибки при удалении из базы данных
                    }
                });
    }

    private void updatePieceInDatabase(PieceInUserList piece, String selectedValue) {
        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("anime").child(String.valueOf(piece.getPieceId()));

        // Обновите значение списка в базе данных
        piece.setUserList(selectedValue);
        animeRef.setValue(piece)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Успешно обновлено в базе данных
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Обработка ошибки при обновлении в базе данных
                    }
                });
    }

    private void addPieceToDatabase(AnimeID anime, String selectedValue) {
        // Создайте новый объект PieceInUserList для добавления в базу данных
        PieceInUserList piece = new PieceInUserList();
        piece.setPieceId(anime.getId());
        piece.setUserEmail(FirebaseAuth.getInstance().
                getCurrentUser().getEmail());
        piece.setUserList(selectedValue);

        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("anime").child(String.valueOf(anime.getId()));

        // Добавьте данные аниме в базу данных
        animeRef.setValue(piece)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Успешно добавлено в базу данных
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Обработка ошибки при добавлении в базу данных
                    }
                });
    }
}