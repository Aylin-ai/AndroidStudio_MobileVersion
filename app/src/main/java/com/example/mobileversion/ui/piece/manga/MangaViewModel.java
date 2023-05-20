package com.example.mobileversion.ui.piece.manga;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import models.Anime;
import models.Genre;
import models.Manga;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaViewModel extends ViewModel {

    private MutableLiveData<List<Manga>> pieceListLiveData = new MutableLiveData<>();
    private MutableLiveData<Genre[]> genreLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    // Другие методы и поля вашего AnimeViewModel...

    public LiveData<List<Manga>> getMangaListLiveData() {
        return pieceListLiveData;
    }

    public LiveData<Genre[]> getGenreLiveData() {
        return genreLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    private OkHttpClient httpClient;
    public List<Manga> pieceList;
    public Genre[] genres;
    private String message;

    public MangaViewModel() {
        httpClient = new OkHttpClient();
        pieceList = new ArrayList<>();
    }

    public void getManga(int page, String order, String type, String status, int genre) {
        Request.Builder requestBuilder = new Request.Builder()
                .url("https://shikimori.me/api/genres")
                .header("Authorization", "User-Agent ShikiOAuthTest");
        Request request = requestBuilder.build();

        // Выполняем запрос на получение жанров
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type genreArrayType = new TypeToken<Genre[]>() {}.getType();
                    genres = gson.fromJson(response.body().string(), genreArrayType);

                    // Обновляем значение LiveData с жанрами
                    genreLiveData.postValue(genres);
                } else {
                    // Обновляем сообщение об ошибке
                    messageLiveData.postValue("Failure");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Обновляем сообщение об ошибке
                messageLiveData.postValue("Error");
            }
        });

        String apiUrl;
        if (genre == 0) {
            apiUrl = String.format("/api/mangas?limit=50&order=%s&page=%d&kind=%s&status=%s", order, page, type, status);
        } else {
            apiUrl = String.format("/api/mangas?limit=50&order=%s&page=%d&kind=%s&status=%s&genre=%d", order, page, type, status, genre);
        }
        request = new Request.Builder()
                .url("https://shikimori.me" + apiUrl)
                .header("Authorization", "User-Agent ShikiOAuthTest")
                .build();

        // Выполняем запрос на получение списка аниме
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONArray pieceJsonArray = null;
                    try {
                        pieceJsonArray = new JSONArray(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Type listType = new TypeToken<List<Manga>>() {}.getType();
                    Gson gson = new Gson();
                    pieceList = gson.fromJson(pieceJsonArray.toString(), listType);
                    for (Manga manga : pieceList) {
                        manga.getImage().setOriginal("https://shikimori.me" + manga.getImage().getOriginal());
                    }
                    // Обновляем значение LiveData с списком аниме
                    pieceListLiveData.postValue(pieceList);
                    // Обновляем сообщение об успешном выполнении
                    messageLiveData.postValue("Success");
                } else {
                    // Обновляем сообщение об ошибке
                    messageLiveData.postValue("Failure");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Обновляем сообщение об ошибке
                messageLiveData.postValue("Error");
            }
        });
    }


    public void getMangas(String searchAnime) {
        try {
            if (searchAnime != null) {
                String apiUrl = String.format("/api/animes?search=%s&limit=50", searchAnime);

                Request.Builder requestBuilder = new Request.Builder()
                        .url("https://shikimori.me" + apiUrl)
                        .header("Authorization", "User-Agent ShikiOAuthTest");
                Request request = requestBuilder.build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Type listType = new TypeToken<List<Anime>>() {}.getType();
                            Gson gson = new Gson();
                            pieceList = gson.fromJson(response.body().string(), listType);
                            for (Manga manga : pieceList) {
                                manga.getImage().setOriginal("https://shikimori.me" + manga.getImage().getOriginal());
                            }
                        } else {
                            // Обработка неуспешного ответа
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Обработка ошибки
                        Log.e("AnimeManager", e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            Log.e("AnimeManager", e.getMessage());
        }

    }

}

