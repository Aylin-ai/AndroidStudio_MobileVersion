package com.example.mobileversion.ui.piece.ranobeID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.mobileversion.ui.piece.mangaID.MangaIDViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.MangaID;
import models.PieceInUserList;
import models.PieceRepository;
import models.Publisher;
import models.RanobeAdapter;

public class RanobeIDActivity extends AppCompatActivity {
    private AnimeAdapter animeAdapter;
    private MangaAdapter mangaAdapter;
    private RanobeAdapter ranobeAdapter;

    private long id = 1;
    private ImageView Image;
    private Spinner pieceSpinner;
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
        setContentView(R.layout.activity_ranobe_id);
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
        pieceSpinner = findViewById(R.id.pieceSpinner);
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getLongExtra("Id", 0);
            ranobeIDViewModel.getRanobe(id);
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

                    loadRanobeFromDatabase(id, new PieceRepository.PieceDataLoadCallback() {
                        @Override
                        public void onDataLoaded(PieceInUserList piece) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    RanobeIDActivity.this,
                                    R.array.spinner_manga_list,
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
                                deletePieceFromDatabase(ranobeIDViewModel.ranobeID);
                            } else {
                                loadRanobeFromDatabase(id, new PieceRepository.PieceDataLoadCallback() {
                                    @Override
                                    public void onDataLoaded(PieceInUserList piece) {
                                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                                RanobeIDActivity.this,
                                                R.array.spinner_manga_list,
                                                android.R.layout.simple_spinner_item
                                        );
                                        if (piece != null) {
                                            updatePieceInDatabase(piece, selectedValue);
                                        } else {
                                            addPieceToDatabase(ranobeIDViewModel.ranobeID, selectedValue);
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

            ranobeIDViewModel.getrelatedAnimeLiveData().observe(this, new Observer<List<Anime>>() {
                @Override
                public void onChanged(List<Anime> animeListData) {
                    // Обновляем адаптер с новыми данными
                    animeAdapter = new AnimeAdapter(animeListData, getApplicationContext());

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
                    mangaAdapter = new MangaAdapter(mangaListData, getApplicationContext());

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
                    ranobeAdapter = new RanobeAdapter(mangaListData, getApplicationContext());

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
                    ranobeAdapter = new RanobeAdapter(mangaListData, getApplicationContext());

                    ranobeAdapter.setOnItemClickListener(new RanobeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga ranobe) {
                            Intent intent = new Intent(com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.this,
                                    com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity.class);

                            intent.putExtra("Id", ranobe.getId());
                            startActivity(intent);
                        }
                    });

                    SimilarRecyclerView.setAdapter(ranobeAdapter);
                }
            });
        }
    }
    public void loadRanobeFromDatabase(long id, PieceRepository.PieceDataLoadCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ranobe")
                .child(String.format("%d %s", id,
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail()
                                .replace('.', ',')));
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
    private void deletePieceFromDatabase(MangaID ranobe) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference animeRef = database.getReference("ranobe")
                .child(String.format("%d %s", ranobe.getId(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail()
                                .replace('.', ',')));
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
        DatabaseReference animeRef = database.getReference("ranobe")
                .child(String.format("%d %s", piece.getPieceId(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail()
                                .replace('.', ',')));

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

    private void addPieceToDatabase(MangaID ranobe, String selectedValue) {
        // Создайте новый объект PieceInUserList для добавления в базу данных
        PieceInUserList piece = new PieceInUserList();
        piece.setPieceId(ranobe.getId());
        piece.setUserEmail(FirebaseAuth.getInstance().
                getCurrentUser().getEmail());
        piece.setUserList(selectedValue);

        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("ranobe")
                .child(String.format("%d %s", ranobe.getId(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail()
                                .replace('.', ',')));

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