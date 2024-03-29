package com.example.mobileversion.ui.piece.ranobeID;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import models.Anime;
import models.Manga;
import models.MangaID;
import models.Related;
import models.Screenshots;
import models.UriTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RanobeIDViewModel extends ViewModel {

    private static final String BASE_URL = "https://shikimori.one";
    private static final String USER_AGENT = "ShikiOAuthTest";
    private static final String RANOBE_ENDPOINT_FORMAT = "/api/ranobe/%s";
    private static final String RELATED_ENDPOINT_FORMAT = "/api/ranobe/%s/related";
    private static final String SIMILAR_ENDPOINT_FORMAT = "/api/ranobe/%s/similar";

    private OkHttpClient httpClient;

    public MangaID ranobeID;
    public List<Screenshots> screens;
    public List<Anime> relatedAnime;
    public List<Manga> relatedManga;
    public List<Manga> relatedRanobe;
    public List<Manga> similar;

    private MutableLiveData<MangaID> ranobeLiveData = new MutableLiveData<>();
    public LiveData<MangaID> getRanobeLiveData() { return ranobeLiveData; }

    private MutableLiveData<List<Anime>> relatedAnimeLiveData = new MutableLiveData<>();
    public LiveData<List<Anime>> getrelatedAnimeLiveData() { return relatedAnimeLiveData; }

    private MutableLiveData<List<Manga>> relatedMangaLiveData = new MutableLiveData<>();
    public LiveData<List<Manga>> getrelatedMangaLiveData() { return relatedMangaLiveData; }

    private MutableLiveData<List<Manga>> relatedRanobeLiveData = new MutableLiveData<>();
    public LiveData<List<Manga>> getrelatedRanobeLiveData() { return relatedRanobeLiveData; }

    private MutableLiveData<List<Manga>> similarLiveData = new MutableLiveData<>();
    public LiveData<List<Manga>> getsimilarLiveData() { return similarLiveData; }

    public RanobeIDViewModel(){
        httpClient = new OkHttpClient();
        screens = new ArrayList<>();
        relatedAnime = new ArrayList<>();
        relatedManga = new ArrayList<>();
        relatedRanobe = new ArrayList<>();
        similar = new ArrayList<>();
    }

    public void getRanobe(long id){
        String animeUrl = BASE_URL + String.format(RANOBE_ENDPOINT_FORMAT, id);
        Request.Builder requestBuilder = new Request.Builder()
                .url(animeUrl)
                .header("Authorization", "User-Agent ShikiOAuthTest");
        Request request = requestBuilder.build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                            .create();
                    Type animeId = new TypeToken<MangaID>() {}.getType();
                    ranobeID = gson.fromJson(response.body().string(), animeId);
                    ranobeID.getImage().setOriginal(BASE_URL + ranobeID.getImage().getOriginal());
                    ranobeID.getImage().setPreview(BASE_URL + ranobeID.getImage().getPreview());
                    ranobeLiveData.postValue(ranobeID);
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = "Провал";
            }
        });

        String relatedUrl = BASE_URL + String.format(RELATED_ENDPOINT_FORMAT, id);
        request = new Request.Builder()
                .url(relatedUrl)
                .header("Authorization", "User-Agent ShikiOAuthTest")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = "Провал";
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONArray relatedJsonArray = null;
                    try {
                        relatedJsonArray = new JSONArray(response.body().string());
                    }
                    catch (JSONException e) {
                        String message = "Связанное";
                    }

                    Type listType = new TypeToken<List<Related>>() {}.getType();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                            .create();
                    List<Related> relatedList = gson.fromJson(relatedJsonArray.toString(), listType);
                    for (Related related : relatedList) {
                        if (related.getAnime() != null) {
                            related.getAnime().getImage().setOriginal(BASE_URL + related.getAnime().getImage().getOriginal());
                            related.getAnime().getImage().setPreview(BASE_URL + related.getAnime().getImage().getPreview());
                            relatedAnime.add(related.getAnime());
                        } else if (related.getManga() != null) {
                            related.getManga().getImage().setOriginal(BASE_URL + related.getManga().getImage().getOriginal());
                            related.getManga().getImage().setPreview(BASE_URL + related.getManga().getImage().getPreview());
                            if (related.getManga().getKind().equals("light_novel") ||
                                    related.getManga().getKind().equals("novel")) {
                                relatedRanobe.add(related.getManga());
                            } else {
                                relatedManga.add(related.getManga());
                            }
                        }
                    }
                    relatedAnimeLiveData.postValue(relatedAnime);
                    relatedMangaLiveData.postValue(relatedManga);
                    relatedRanobeLiveData.postValue(relatedRanobe);
                }
            }
        });

        String similarUrl = BASE_URL + String.format(SIMILAR_ENDPOINT_FORMAT, id);
        request = new Request.Builder()
                .url(similarUrl)
                .header("Authorization", "User-Agent ShikiOAuthTest")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = "Провал";
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONArray similarJsonArray = null;
                    try {
                        similarJsonArray = new JSONArray(response.body().string());
                    } catch (JSONException e) {
                        String message = "Похожее";
                    }
                    Type listType = new TypeToken<List<Manga>>() {}.getType();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                            .create();
                    List<Manga> similarList = gson.fromJson(similarJsonArray.toString(), listType);
                    for (Manga ranobe : similarList) {
                        ranobe.getImage().setOriginal("https://shikimori.one" + ranobe.getImage().getOriginal());
                        if (ranobe.getKind().equals("light_novel") || ranobe.getKind().equals("novel")) {
                            similar.add(ranobe);
                        }
                    }
                    similarLiveData.postValue(similar);
                }
            }
        });
    }

}