package com.example.mobileversion.ui.piece.ranobe;

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

public class RanobeViewModel extends ViewModel {

    private MutableLiveData<List<Manga>> listLiveData = new MutableLiveData<>();
    private MutableLiveData<Genre[]> genreLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    // Другие методы и поля вашего AnimeViewModel...

    public LiveData<List<Manga>> getListLiveData() {
        return listLiveData;
    }

    public LiveData<Genre[]> getGenreLiveData() {
        return genreLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    private OkHttpClient httpClient;
    public List<Manga> list;
    public Genre[] genres;
    private String message;

    private String selectedOrder;
    public String getSelectedOrder() { return selectedOrder; }
    public void setSelectedOrder(String selectedOrder) { this.selectedOrder = selectedOrder; }

    private String selectedStatus;
    public String getSelectedStatus() { return selectedStatus; }
    public void setSelectedStatus(String selectedStatus) { this.selectedStatus = selectedStatus; }

    private long selectedGenre;
    public long getSelectedGenre() { return selectedGenre; }
    public void setSelectedGenre(long selectedGenre) { this.selectedGenre = selectedGenre; }

    private int selectedPage;
    public int getSelectedPage() { return selectedPage; }
    public void setSelectedPage(int selectedPage) { this.selectedPage = selectedPage; }

    public RanobeViewModel() {
        httpClient = new OkHttpClient();
        list = new ArrayList<>();
    }

    public void getRanobe(int page, String order, String status, long genre) {
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
            apiUrl = String.format("/api/ranobe?limit=50&order=%s&page=%d&status=%s", order, page, status);
        } else {
            apiUrl = String.format("/api/ranobe?limit=50&order=%s&page=%d&status=%s&genre=%d", order, page, status, genre);
        }
        request = new Request.Builder()
                .url("https://shikimori.me" + apiUrl)
                .header("Authorization", "User-Agent ShikiOAuthTest")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Type listType = new TypeToken<List<Manga>>() {}.getType();
                    Gson gson = new Gson();
                    list = gson.fromJson(jsonArray.toString(), listType);
                    for (Manga ranobe : list) {
                        ranobe.getImage().setOriginal("https://shikimori.me" + ranobe.getImage().getOriginal());
                    }
                    // Обновляем значение LiveData с списком аниме
                    listLiveData.postValue(list);
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


    public void getRanobe(String search) {
        try {
            if (search != null) {
                String apiUrl = String.format("/api/ranobe?search=%s&limit=50", search);

                Request.Builder requestBuilder = new Request.Builder()
                        .url("https://shikimori.me" + apiUrl)
                        .header("Authorization", "User-Agent ShikiOAuthTest");
                Request request = requestBuilder.build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response.body().string());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Type listType = new TypeToken<List<Manga>>() {}.getType();
                            Gson gson = new Gson();
                            list = gson.fromJson(jsonArray.toString(), listType);
                            for (Manga ranobe : list) {
                                ranobe.getImage().setOriginal("https://shikimori.me" + ranobe.getImage().getOriginal());
                            }
                            // Обновляем значение LiveData с списком аниме
                            listLiveData.postValue(list);
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
            Log.e("MangaManager", e.getMessage());
        }

    }

}