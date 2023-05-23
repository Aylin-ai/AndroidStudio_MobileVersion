package com.example.mobileversion.ui.piece.manga;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentMangaBinding;
import com.example.mobileversion.ui.piece.anime.AnimeViewModel;
import com.example.mobileversion.ui.piece.animeID.AnimeIDActivity;
import com.example.mobileversion.ui.piece.mangaID.MangaIDActivity;
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
import models.Manga;
import models.MangaAdapter;

public class MangaFragment extends Fragment {
    private RecyclerView pieceList;
    private MangaAdapter adapter;
    private FragmentMangaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);

        binding = FragmentMangaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pieceList = root.findViewById(R.id.mangaList);
        pieceList.setLayoutManager(new LinearLayoutManager(getContext()));


        mangaViewModel.setSelectedPage(1);
        mangaViewModel.setSelectedOrder("ranked");
        mangaViewModel.setSelectedKind("");
        mangaViewModel.setSelectedStatus("");
        mangaViewModel.setSelectedGenre(0);

        mangaViewModel.getManga(mangaViewModel.getSelectedPage(),
                mangaViewModel.getSelectedOrder(),
                mangaViewModel.getSelectedKind(),
                mangaViewModel.getSelectedStatus(),
                mangaViewModel.getSelectedGenre());
        mangaViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
            @Override
            public void onChanged(List<Manga> mangas) {
                adapter = new MangaAdapter(mangas);

                adapter.setOnItemClickListener(new MangaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Manga manga) {
                        Intent intent = new Intent(getActivity(), MangaIDActivity.class);

                        intent.putExtra("Id", manga.getId());
                        startActivity(intent);
                    }
                });

                pieceList.setAdapter(adapter);
            }
        });

        mangaViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.anime_filtr_menu, popupMenu.getMenu());

        // Обработчик клика на пункты меню
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Обработка выбора пунктов меню
                switch (item.getItemId()) {
                    case R.id.menu_genres:
                        showDropdownList(adapter, mangaViewModel.genres);
                        return true;
                    case R.id.menu_status:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_manga_status));
                        return true;
                    case R.id.menu_kinds:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_manga_kind));
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

    private void showDropdownList(MangaAdapter mangaAdapter, String[] options) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите опцию");

        // Создание и настройка адаптера для списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, options);

        // Создание Spinner и установка адаптера
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(adapter);

        int selectedPosition = 0;
        String selectedItem = "";
        if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_piece_order))) {
            switch (mangaViewModel.getSelectedOrder()) {
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
        } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_manga_status))) {
            switch (mangaViewModel.getSelectedStatus()) {
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
                    selectedItem = "Вышла";
                    break;
                case "paused":
                    selectedItem = "Заморожена";
                    break;
                case "discontinued":
                    selectedItem = "Остановлена";
                    break;
            }
            selectedPosition = Arrays.asList(options).indexOf(selectedItem);
        } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_manga_kind))) {
            switch (mangaViewModel.getSelectedKind()) {
                case "":
                    selectedItem = "Всё";
                    break;
                case "manga":
                    selectedItem = "Манга";
                    break;
                case "manhva":
                    selectedItem = "Манхва";
                    break;
                case "manhua":
                    selectedItem = "Манхуа";
                    break;
                case "one_shot":
                    selectedItem = "One Shot";
                    break;
                case "doujin":
                    selectedItem = "Додзинси";
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
                if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_piece_order))) {
                    switch (selectedOption) {
                        case "По рейтингу":
                            mangaViewModel.setSelectedOrder("ranked");
                            break;
                        case "По типу":
                            mangaViewModel.setSelectedOrder("kind");
                            break;
                        case "По популярности":
                            mangaViewModel.setSelectedOrder("popularity");
                            break;
                        case "По имени":
                            mangaViewModel.setSelectedOrder("name");
                            break;
                        case "По дате релиза":
                            mangaViewModel.setSelectedOrder("aired_on");
                            break;
                        case "По статусу":
                            mangaViewModel.setSelectedOrder("status");
                            break;
                        case "Случайно":
                            mangaViewModel.setSelectedOrder("random");
                            break;
                    }
                } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_manga_status))) {
                    switch (selectedOption) {
                        case "Всё":
                            mangaViewModel.setSelectedStatus("");
                            break;
                        case "Анонс":
                            mangaViewModel.setSelectedStatus("anons");
                            break;
                        case "Онгоинг":
                            mangaViewModel.setSelectedStatus("ongoing");
                            break;
                        case "Вышла":
                            mangaViewModel.setSelectedStatus("released");
                            break;
                        case "Заморожена":
                            mangaViewModel.setSelectedStatus("paused");
                            break;
                        case "Остановлена":
                            mangaViewModel.setSelectedStatus("discontinued");
                            break;
                    }
                } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_manga_kind))) {
                    switch (selectedOption) {
                        case "Всё":
                            mangaViewModel.setSelectedKind("");
                            break;
                        case "Манга":
                            mangaViewModel.setSelectedKind("manga");
                            break;
                        case "Манхва":
                            mangaViewModel.setSelectedKind("manhva");
                            break;
                        case "Манхуа":
                            mangaViewModel.setSelectedKind("manhua");
                            break;
                        case "One Shot":
                            mangaViewModel.setSelectedKind("one_shot");
                            break;
                        case "Додзинси":
                            mangaViewModel.setSelectedKind("doujin");
                            break;
                    }
                }
                mangaViewModel.getManga(mangaViewModel.getSelectedPage(),
                        mangaViewModel.getSelectedOrder(),
                        mangaViewModel.getSelectedKind(),
                        mangaViewModel.getSelectedStatus(),
                        mangaViewModel.getSelectedGenre());
                mangaViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        mangaAdapter.setMangaList(listData);
                        mangaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(MangaAdapter mangaAdapter, Genre[] options) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);
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

        int selectedPosition = listId.indexOf(mangaViewModel.getSelectedGenre());
        spinner.setSelection(selectedPosition);

        // Установка Spinner в диалоговое окно
        builder.setView(spinner);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из Spinner
                String selectedOption = (String) spinner.getSelectedItem();

                for (int i = 0; i < list.size(); i++) {
                    if (Objects.equals(selectedOption, list.get(i))) {
                        mangaViewModel.setSelectedGenre(listId.get(i));
                    }
                }

                mangaViewModel.getManga(mangaViewModel.getSelectedPage(),
                        mangaViewModel.getSelectedOrder(),
                        mangaViewModel.getSelectedKind(),
                        mangaViewModel.getSelectedStatus(),
                        mangaViewModel.getSelectedGenre());
                mangaViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        mangaAdapter.setMangaList(listData);
                        mangaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(MangaAdapter mangaAdapter, int[] options) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);
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

        int selectedPosition = mangaViewModel.getSelectedPage() - 1;
        spinner.setSelection(selectedPosition);

        // Установка Spinner в диалоговое окно
        builder.setView(spinner);

        // Установка кнопок "OK" и "Отмена"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Получение выбранного значения из Spinner
                String selectedOption = (String) spinner.getSelectedItem();

                for (int i = 0; i < options.length; i++) {
                    if (Objects.equals(selectedOption, stringOptions[i])) {
                        mangaViewModel.setSelectedPage(options[i]);
                    }
                }

                mangaViewModel.getManga(mangaViewModel.getSelectedPage(),
                        mangaViewModel.getSelectedOrder(),
                        mangaViewModel.getSelectedKind(),
                        mangaViewModel.getSelectedStatus(),
                        mangaViewModel.getSelectedGenre());
                mangaViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        mangaAdapter.setMangaList(listData);
                        mangaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(MangaAdapter mangaAdapter) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);
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

                mangaViewModel.getManga(enteredText);
                mangaViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        mangaAdapter.setMangaList(listData);
                        mangaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }
}
