package com.example.mobileversion.ui.userlist.manga;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import models.Anime;
import models.AnimeID;
import models.Manga;
import models.PieceInUserList;
import models.UriTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserMangaViewModel extends ViewModel {
    private MutableLiveData<List<Manga>> mangaListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private MutableLiveData<List<PieceInUserList>> piecesListLiveData = new MutableLiveData<>();
    public LiveData<List<Manga>> getMangaListLiveData() {
        return mangaListLiveData;
    }
    private OkHttpClient httpClient;
    private String message;

    public UserMangaViewModel() {
        httpClient = new OkHttpClient();
    }

    public void loadMangaFromDatabase(String listValue) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("manga");
        Query query = myRef.orderByChild("userEmail");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PieceInUserList> pieces = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    PieceInUserList piece = childSnapshot.getValue(PieceInUserList.class);
                    if (piece != null && piece.getUserList().equals(listValue) &&
                            piece.getUserEmail()
                                    .equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        pieces.add(piece);
                    }
                }
                piecesListLiveData.postValue(pieces);
                getMangas(pieces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    public void getMangas(List<PieceInUserList> pieceList) {
        try {
            if (pieceList.size() != 0) {
                int totalPieces = pieceList.size();
                AtomicInteger loadedPieces = new AtomicInteger(0);

                for (PieceInUserList piece : pieceList) {
                    String apiUrl = String.format("/api/mangas/%d", piece.getPieceId());

                    Request.Builder requestBuilder = new Request.Builder()
                            .url("https://shikimori.one" + apiUrl)
                            .header("Authorization", "User-Agent ShikiOAuthTest");
                    Request request = requestBuilder.build();

                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            List<Manga> mangaList = new ArrayList<>();
                            if (response.isSuccessful()) {
                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                                        .create();
                                Type animeType = new TypeToken<Manga>() {}.getType();
                                Manga manga = gson.fromJson(response.body().string(), animeType);
                                manga.getImage().setOriginal("https://shikimori.one" + manga.getImage().getOriginal());
                                manga.getImage().setPreview("https://shikimori.one" + manga.getImage().getPreview());
                                mangaList.add(manga);
                            } else {
                                // Обновляем сообщение об ошибке
                                messageLiveData.postValue("Failure");
                            }

                            int loadedCount = loadedPieces.incrementAndGet();
                            if (loadedCount == totalPieces) {
                                // Все данные загружены, поэтому обновляем LiveData
                                mangaListLiveData.postValue(mangaList);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Обновляем сообщение об ошибке
                            messageLiveData.postValue("Error");
                            List<Manga> mangaList = new ArrayList<>();
                            int loadedCount = loadedPieces.incrementAndGet();
                            if (loadedCount == totalPieces) {
                                // Все данные загружены, поэтому обновляем LiveData
                                mangaListLiveData.postValue(mangaList);
                            }
                        }
                    });
                }
            } else {
                mangaListLiveData.postValue(new ArrayList<>());
            }
        } catch (Exception e) {
            Log.e("Manager", e.getMessage());
        }
    }

}