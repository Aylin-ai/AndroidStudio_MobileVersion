package com.example.mobileversion.ui.piece.anime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentAnimeBinding;
import com.example.mobileversion.ui.piece.animeID.AnimeIDActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import models.Anime;
import models.AnimeAdapter;
import models.AnimeID;
import models.Genre;

public class AnimeFragment extends Fragment {
    private RecyclerView animeList;
    private AnimeAdapter adapter;
    private FragmentAnimeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        animeList = root.findViewById(R.id.animeList);
        animeList.setLayoutManager(new LinearLayoutManager(getContext()));

        animeViewModel.setSelectedPage(1);
        animeViewModel.setSelectedOrder("ranked");
        animeViewModel.setSelectedKind("");
        animeViewModel.setSelectedStatus("");
        animeViewModel.setSelectedGenre(0);

        animeViewModel.getAnimes(animeViewModel.getSelectedPage(),
                animeViewModel.getSelectedOrder(),
                animeViewModel.getSelectedKind(),
                animeViewModel.getSelectedStatus(),
                animeViewModel.getSelectedGenre());
        animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
            @Override
            public void onChanged(List<Anime> animeListData) {
                // Обновляем адаптер с новыми данными
                adapter = new AnimeAdapter(animeListData);

                adapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Anime anime) {
                        // Обработайте нажатие кнопки для выбранного аниме
                        Intent intent = new Intent(getActivity(), AnimeIDActivity.class);

                        intent.putExtra("AnimeId", anime.getId());
                        startActivity(intent);
                    }
                });

                animeList.setAdapter(adapter);
            }
        });

        animeViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                // Обрабатываем сообщение
            }
        });

        FloatingActionButton filtr_button = root.findViewById(R.id.filtrButton); // Находите кнопку по ее идентификатору

        filtr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(filtr_button);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showPopupMenu(View anchorView) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.anime_filtr_menu, popupMenu.getMenu());

        // Обработчик клика на пункты меню
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Обработка выбора пунктов меню
                switch (item.getItemId()) {
                    case R.id.menu_genres:
                        showDropdownList(adapter, animeViewModel.animeGenres);
                        return true;
                    case R.id.menu_status:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_anime_status));
                        return true;
                    case R.id.menu_kinds:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_anime_kind));
                        return true;
                    case R.id.menu_orders:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_piece_order));
                        return true;
                    case R.id.menu_pages:
                        showDropdownList(adapter, IntStream.rangeClosed(1, 400).toArray());
                        return true;
                    case R.id.menu_search:
                        showDropdownList(adapter);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void showDropdownList(AnimeAdapter animeAdapter, String[] options) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите опцию");

        // Создание и настройка адаптера для списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, options);

        // Создание Spinner и установка адаптера
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(adapter);

        int selectedPosition = 0;
        String selectedItem = "";
        if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_piece_order))){
            switch (animeViewModel.getSelectedOrder()){
                case "ranked":
                    selectedItem = "По рейтингу";
                    break;
                case "kind":
                    selectedItem = "По типу";
                    break;
                case "popularity":
                    selectedItem = "По популярности";
                    break;
                case "name":
                    selectedItem = "По имени";
                    break;
                case "aired_on":
                    selectedItem = "По дате релиза";
                    break;
                case "status":
                    selectedItem = "По статусу";
                    break;
                case "random":
                    selectedItem = "Случайно";
                    break;
            }
            selectedPosition = Arrays.asList(options).indexOf(selectedItem);
        } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_anime_status))) {
            switch (animeViewModel.getSelectedStatus()){
                case "":
                    selectedItem = "Всё";
                    break;
                case "anons":
                    selectedItem = "Анонс";
                    break;
                case "ongoing":
                    selectedItem = "Онгоинг";
                    break;
                case "released":
                    selectedItem = "Вышел";
                    break;
            }
            selectedPosition = Arrays.asList(options).indexOf(selectedItem);
        } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_anime_kind))) {
            switch (animeViewModel.getSelectedKind()){
                case "":
                    selectedItem = "Всё";
                    break;
                case "tv":
                    selectedItem = "ТВ";
                    break;
                case "movie":
                    selectedItem = "Фильм";
                    break;
                case "ova":
                    selectedItem = "OVA";
                    break;
                case "ona":
                    selectedItem = "ONA";
                    break;
                case "tv_13":
                    selectedItem = "TV 13 серий";
                    break;
                case "tv_24":
                    selectedItem = "TV 24 серии";
                    break;
                case "tv_48":
                    selectedItem = "TV 48 серий";
                    break;
            }
            selectedPosition = Arrays.asList(options).indexOf(selectedItem);
        }
        spinner.setSelection(selectedPosition);

        // Установка Spinner в диалоговое окно
        builder.setView(spinner);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из Spinner
                String selectedOption = (String) spinner.getSelectedItem();
                // Выполнение действий с выбранным значением
                if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_piece_order))){
                    switch (selectedOption){
                        case "По рейтингу":
                            animeViewModel.setSelectedOrder("ranked");
                            break;
                        case "По типу":
                            animeViewModel.setSelectedOrder("kind");
                            break;
                        case "По популярности":
                            animeViewModel.setSelectedOrder("popularity");
                            break;
                        case "По имени":
                            animeViewModel.setSelectedOrder("name");
                            break;
                        case "По дате релиза":
                            animeViewModel.setSelectedOrder("aired_on");
                            break;
                        case "По статусу":
                            animeViewModel.setSelectedOrder("status");
                            break;
                        case "Случайно":
                            animeViewModel.setSelectedOrder("random");
                            break;
                    }
                } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_anime_status))) {
                    switch (selectedOption){
                        case "Всё":
                            animeViewModel.setSelectedStatus("");
                            break;
                        case "Анонс":
                            animeViewModel.setSelectedStatus("anons");
                            break;
                        case "Онгоинг":
                            animeViewModel.setSelectedStatus("ongoing");
                            break;
                        case "Вышел":
                            animeViewModel.setSelectedStatus("released");
                            break;
                    }
                } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_anime_kind))) {
                    switch (selectedOption){
                        case "Всё":
                            animeViewModel.setSelectedKind("");
                            break;
                        case "ТВ":
                            animeViewModel.setSelectedKind("tv");
                            break;
                        case "Фильм":
                            animeViewModel.setSelectedKind("movie");
                            break;
                        case "OVA":
                            animeViewModel.setSelectedKind("ova");
                            break;
                        case "ONA":
                            animeViewModel.setSelectedKind("ona");
                            break;
                        case "TV 13 серий":
                            animeViewModel.setSelectedKind("tv_13");
                            break;
                        case "TV 24 серии":
                            animeViewModel.setSelectedKind("tv_24");
                            break;
                        case "TV 48 серий":
                            animeViewModel.setSelectedKind("tv_48");
                            break;
                    }
                }
                animeViewModel.getAnimes(animeViewModel.getSelectedPage(),
                        animeViewModel.getSelectedOrder(),
                        animeViewModel.getSelectedKind(),
                        animeViewModel.getSelectedStatus(),
                        animeViewModel.getSelectedGenre());
                animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
                    @Override
                    public void onChanged(List<Anime> animeListData) {
                        // Обновляем адаптер с новыми данными
                        animeAdapter.setAnimeList(animeListData);
                        animeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(AnimeAdapter animeAdapter, Genre[] options) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите опцию");

        ArrayList<Long> listId = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();

        listId.add(0L);
        list.add("Всё");
        for (Genre option : options) {
            listId.add(option.getId());
            list.add(option.getRussian());
        }

        // Создание и настройка адаптера для списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);

        // Создание Spinner и установка адаптера
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(adapter);

        int selectedPosition = listId.indexOf(animeViewModel.getSelectedGenre());
        spinner.setSelection(selectedPosition);

        // Установка Spinner в диалоговое окно
        builder.setView(spinner);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из Spinner
                String selectedOption = (String) spinner.getSelectedItem();

                for (int i = 0; i < list.size(); i++){
                    if (Objects.equals(selectedOption, list.get(i))){
                        animeViewModel.setSelectedGenre(listId.get(i));
                    }
                }

                animeViewModel.getAnimes(animeViewModel.getSelectedPage(),
                        animeViewModel.getSelectedOrder(),
                        animeViewModel.getSelectedKind(),
                        animeViewModel.getSelectedStatus(),
                        animeViewModel.getSelectedGenre());
                animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
                    @Override
                    public void onChanged(List<Anime> animeListData) {
                        // Обновляем адаптер с новыми данными
                        animeAdapter.setAnimeList(animeListData);
                        animeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(AnimeAdapter animeAdapter, int[] options) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите опцию");

        String[] stringOptions = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            stringOptions[i] = String.valueOf(options[i]);
        }

        // Создание и настройка адаптера для списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stringOptions);

        // Создание Spinner и установка адаптера
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(adapter);

        int selectedPosition = animeViewModel.getSelectedPage() - 1;
        spinner.setSelection(selectedPosition);

        // Установка Spinner в диалоговое окно
        builder.setView(spinner);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из Spinner
                String selectedOption = (String) spinner.getSelectedItem();

                for (int i = 0; i < options.length; i++){
                    if (Objects.equals(selectedOption, stringOptions[i])){
                        animeViewModel.setSelectedPage(options[i]);
                    }
                }

                animeViewModel.getAnimes(animeViewModel.getSelectedPage(),
                        animeViewModel.getSelectedOrder(),
                        animeViewModel.getSelectedKind(),
                        animeViewModel.getSelectedStatus(),
                        animeViewModel.getSelectedGenre());
                animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
                    @Override
                    public void onChanged(List<Anime> animeListData) {
                        // Обновляем адаптер с новыми данными
                        animeAdapter.setAnimeList(animeListData);
                        animeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(AnimeAdapter animeAdapter) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Введите название");

        // Создание TextView
        final EditText editText = new EditText(getActivity());
        // Настройка параметров TextView
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setPadding(16, 16, 16, 16);
        editText.setTextSize(20);

        // Установка TextView в диалоговое окно
        builder.setView(editText);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из TextView
                String enteredText = editText.getText().toString();

                animeViewModel.getAnimes(enteredText);
                animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
                    @Override
                    public void onChanged(List<Anime> animeListData) {
                        // Обновляем адаптер с новыми данными
                        animeAdapter.setAnimeList(animeListData);
                        animeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }


}
