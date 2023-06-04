package com.example.mobileversion.ui.userlist.anime;

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
import models.PieceInUserList;
import models.UriTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserAnimeViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> animeListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private MutableLiveData<List<PieceInUserList>> piecesListLiveData = new MutableLiveData<>();


    public LiveData<List<Anime>> getAnimeListLiveData() {
        return animeListLiveData;
    }
    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }
    public LiveData<List<PieceInUserList>> getPiecesListLiveData() { return piecesListLiveData; }

    public OkHttpClient httpClient;
    public List<AnimeID> animeList;
    public List<PieceInUserList> pieceList;
    private String message;

    public UserAnimeViewModel() {
        httpClient = new OkHttpClient();
        animeList = new ArrayList<>();
    }

    public void loadAnimeFromDatabase(String listValue) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("anime");
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
                getAnimes(pieces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    public void getAnimes(List<PieceInUserList> pieceList) {
        try {
            if (pieceList.size() != 0) {
                int totalPieces = pieceList.size();
                AtomicInteger loadedPieces = new AtomicInteger(0);
                List<Anime> animeList = new ArrayList<>();

                for (PieceInUserList piece : pieceList) {
                    String apiUrl = String.format("/api/animes/%d", piece.getPieceId());

                    Request.Builder requestBuilder = new Request.Builder()
                            .url("https://shikimori.me" + apiUrl)
                            .header("Authorization", "User-Agent ShikiOAuthTest");
                    Request request = requestBuilder.build();

                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                                        .create();
                                Type animeType = new TypeToken<Anime>() {}.getType();
                                Anime anime = gson.fromJson(response.body().string(), animeType);
                                anime.getImage().setOriginal("https://shikimori.me" + anime.getImage().getOriginal());
                                anime.getImage().setPreview("https://shikimori.me" + anime.getImage().getPreview());
                                animeList.add(anime);
                            } else {
                                // Обновляем сообщение об ошибке
                                messageLiveData.postValue("Failure");
                            }

                            int loadedCount = loadedPieces.incrementAndGet();
                            if (loadedCount == totalPieces) {
                                // Все данные загружены, поэтому обновляем LiveData
                                animeListLiveData.postValue(animeList);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Обновляем сообщение об ошибке
                            messageLiveData.postValue("Error");

                            int loadedCount = loadedPieces.incrementAndGet();
                            if (loadedCount == totalPieces) {
                                // Все данные загружены, поэтому обновляем LiveData
                                animeListLiveData.postValue(animeList);
                            }
                        }
                    });
                }
            } else {
                animeListLiveData.postValue(new ArrayList<>());
            }
        } catch (Exception e) {
            Log.e("AnimeManager", e.getMessage());
        }
    }

}
