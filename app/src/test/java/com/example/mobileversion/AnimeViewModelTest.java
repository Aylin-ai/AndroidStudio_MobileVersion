package com.example.mobileversion;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileversion.ui.piece.anime.AnimeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import models.Anime;
import models.Genre;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@RunWith(MockitoJUnitRunner.class)
public class AnimeViewModelTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private OkHttpClient httpClient;

    @Mock
    private Call call;

    @Mock
    private MutableLiveData<List<Anime>> animeListLiveData;

    @Mock
    private MutableLiveData<Genre[]> genreLiveData;

    @Mock
    private MutableLiveData<String> messageLiveData;

    @Captor
    private ArgumentCaptor<Callback> callbackCaptor;

    private AnimeViewModel animeViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        animeViewModel = new AnimeViewModel();
        animeViewModel.animeListLiveData = animeListLiveData;
        animeViewModel.genreLiveData = genreLiveData;
        animeViewModel.messageLiveData = messageLiveData;
        animeViewModel.httpClient = httpClient;
    }

    // Тест для метода getAnimeListLiveData
    @Test
    public void testGetAnimeListLiveData() {
        LiveData<List<Anime>> animeListLiveData = animeViewModel.getAnimeListLiveData();
        assertEquals(animeListLiveData, this.animeListLiveData);
    }

    // Тест для метода getGenreLiveData
    @Test
    public void testGetGenreLiveData() {
        LiveData<Genre[]> genreLiveData = animeViewModel.getGenreLiveData();
        assertEquals(genreLiveData, this.genreLiveData);
    }

    // Тест для метода getMessageLiveData
    @Test
    public void testGetMessageLiveData() {
        LiveData<String> messageLiveData = animeViewModel.getMessageLiveData();
        assertEquals(messageLiveData, this.messageLiveData);
    }

    // Тест для метода setSelectedOrder
    @Test
    public void testSetSelectedOrder() {
        String selectedOrder = "newest";
        animeViewModel.setSelectedOrder(selectedOrder);
        assertEquals(selectedOrder, animeViewModel.getSelectedOrder());
    }

    // Тест для метода setSelectedKind
    @Test
    public void testSetSelectedKind() {
        String selectedKind = "TV";
        animeViewModel.setSelectedKind(selectedKind);
        assertEquals(selectedKind, animeViewModel.getSelectedKind());
    }

    // Тест для метода setSelectedStatus
    @Test
    public void testSetSelectedStatus() {
        String selectedStatus = "finished";
        animeViewModel.setSelectedStatus(selectedStatus);
        assertEquals(selectedStatus, animeViewModel.getSelectedStatus());
    }

    // Тест для метода setSelectedGenre
    @Test
    public void testSetSelectedGenre() {
        long selectedGenre = 1L;
        animeViewModel.setSelectedGenre(selectedGenre);
        assertEquals(selectedGenre, animeViewModel.getSelectedGenre());
    }

    // Тест для метода setSelectedPage
    @Test
    public void testSetSelectedPage() {
        int selectedPage = 1;
        animeViewModel.setSelectedPage(selectedPage);
        assertEquals(selectedPage, animeViewModel.getSelectedPage());
    }

    // Тест для метода getAnimes с параметрами page, order, type, status и genre
    @Test
    public void testGetAnimesWithParams() throws Exception {
        int page = 1;
        String order = "ranked";
        String type = "";
        String status = "";
        long genre = 0L;

        String expectedUrl = String.format("https://shikimori.me/api/animes?limit=50&order=%s&page=%d&kind=%s&status=%s&genre=%d",
                order, page, type, status, genre);

        Request request1 = new Request.Builder().url(expectedUrl).build();

        // Устанавливаем поведение для HTTP клиента, чтобы он возвращал успешный Response
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
        Response response = new Response.Builder()
                .request(request1)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody)
                .build();
        when(httpClient.newCall(eq(request1))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // Вызываем метод getAnimes
        animeViewModel.getAnimes(page, order, type, status, genre);

        // Проверяем, что URL для запроса был сформирован правильно
        verify(httpClient).newCall(argThat(request -> request.url().toString().equals(expectedUrl)));

        // Проверяем, что метод execute был вызван на HTTP клиенте
        verify(call).execute();

        // Проверяем, что сообщение об успешном выполнении было правильно обновлено в LiveData
        verify(messageLiveData).postValue("Success");
    }

    // Тест для метода getAnimes с параметром searchAnime
    @Test
    public void testGetAnimesWithSearch() throws Exception {
        String searchAnime = "example";

        String expectedUrl = String.format("https://shikimori.me/api/animes?search=%s&limit=50", searchAnime);

        Request request1 = new Request.Builder().url(expectedUrl).build();

        // Устанавливаем поведение для HTTP клиента, чтобы он возвращал успешный Response
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
        Response response = new Response.Builder()
                .request(request1)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody)
                .build();
        when(httpClient.newCall(eq(request1))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // Вызываем метод getAnimes
        animeViewModel.getAnimes(searchAnime);

        // Проверяем, что URL для запроса был сформирован правильно
        verify(httpClient).newCall(argThat(request -> request.url().toString().equals(expectedUrl)));

        // Проверяем, что метод execute был вызван на HTTP клиенте
        verify(call).execute();

        // Проверяем, что сообщение об успешном выполнении было правильно обновлено в LiveData
        verify(messageLiveData).postValue("Success");
    }

}

