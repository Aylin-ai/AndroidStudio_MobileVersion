package com.example.mobileversion.ui.piece.anime;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

import models.Anime;
import models.Genre;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnimeViewModel extends ViewModel {

    public MutableLiveData<List<Anime>> animeListLiveData = new MutableLiveData<>();
    public MutableLiveData<Genre[]> genreLiveData = new MutableLiveData<>();
    public MutableLiveData<String> messageLiveData = new MutableLiveData<>();


    public LiveData<List<Anime>> getAnimeListLiveData() {
        return animeListLiveData;
    }

    public LiveData<Genre[]> getGenreLiveData() {
        return genreLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public OkHttpClient httpClient;
    public List<Anime> animeList;
    public Genre[] animeGenres;
    private String message;

    private String selectedOrder;
    public String getSelectedOrder() { return selectedOrder; }
    public void setSelectedOrder(String selectedOrder) { this.selectedOrder = selectedOrder; }

    private String selectedKind;
    public String getSelectedKind() { return selectedKind; }
    public void setSelectedKind(String selectedKind) { this.selectedKind = selectedKind; }

    private String selectedStatus;
    public String getSelectedStatus() { return selectedStatus; }
    public void setSelectedStatus(String selectedStatus) { this.selectedStatus = selectedStatus; }

    private long selectedGenre;
    public long getSelectedGenre() { return selectedGenre; }
    public void setSelectedGenre(long selectedGenre) { this.selectedGenre = selectedGenre; }

    private int selectedPage;
    public int getSelectedPage() { return selectedPage; }
    public void setSelectedPage(int selectedPage) { this.selectedPage = selectedPage; }

    public AnimeViewModel() {
        httpClient = new OkHttpClient();
        animeList = new ArrayList<>();
    }

    public void getAnimes(int page, String order, String type, String status, long genre) {
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
                    animeGenres = gson.fromJson(response.body().string(), genreArrayType);

                    // Обновляем значение LiveData с жанрами
                    genreLiveData.postValue(animeGenres);
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
            apiUrl = String.format("/api/animes?limit=50&order=%s&page=%d&kind=%s&status=%s", order, page, type, status);
        } else {
            apiUrl = String.format("/api/animes?limit=50&order=%s&page=%d&kind=%s&status=%s&genre=%d", order, page, type, status, genre);
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
                    JSONArray animeJsonArray = null;
                    try {
                        animeJsonArray = new JSONArray(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Type listType = new TypeToken<List<Anime>>() {}.getType();
                    Gson gson = new Gson();
                    animeList = gson.fromJson(animeJsonArray.toString(), listType);
                    for (Anime anime : animeList) {
                        anime.getImage().setOriginal("https://shikimori.me" + anime.getImage().getOriginal());
                    }
                    // Обновляем значение LiveData с списком аниме
                    animeListLiveData.postValue(animeList);
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


    public void getAnimes(String searchAnime) {
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
                            JSONArray animeJsonArray = null;
                            try {
                                animeJsonArray = new JSONArray(response.body().string());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Type listType = new TypeToken<List<Anime>>() {}.getType();
                            Gson gson = new Gson();
                            animeList = gson.fromJson(animeJsonArray.toString(), listType);
                            for (Anime anime : animeList) {
                                anime.getImage().setOriginal("https://shikimori.me" + anime.getImage().getOriginal());
                            }
                            // Обновляем значение LiveData с списком аниме
                            animeListLiveData.postValue(animeList);
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
        } catch (Exception e) {
            Log.e("AnimeManager", e.getMessage());
        }

    }

}
