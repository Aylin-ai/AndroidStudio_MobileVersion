package com.example.mobileversion.ui.piece.ranobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentMangaBinding;
import com.example.mobileversion.databinding.FragmentRanobeBinding;
import com.example.mobileversion.ui.piece.animeID.AnimeIDActivity;
import com.example.mobileversion.ui.piece.manga.MangaViewModel;
import com.example.mobileversion.ui.piece.mangaID.MangaIDActivity;
import com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import models.Anime;
import models.AnimeAdapter;
import models.Genre;
import models.Manga;
import models.MangaAdapter;
import models.RanobeAdapter;

public class RanobeFragment extends Fragment {
    private RecyclerView pieceList;
    private RanobeAdapter adapter;
    private FragmentMangaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);

        binding = FragmentMangaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pieceList = root.findViewById(R.id.mangaList);
        pieceList.setLayoutManager(new LinearLayoutManager(getContext()));


        ranobeViewModel.setSelectedPage(1);
        ranobeViewModel.setSelectedOrder("ranked");
        ranobeViewModel.setSelectedStatus("");
        ranobeViewModel.setSelectedGenre(0);

        ranobeViewModel.getRanobe(ranobeViewModel.getSelectedPage(),
                ranobeViewModel.getSelectedOrder(),
                ranobeViewModel.getSelectedStatus(),
                ranobeViewModel.getSelectedGenre());
        ranobeViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
            @Override
            public void onChanged(List<Manga> ranobes) {
                adapter = new RanobeAdapter(ranobes, getContext());
                adapter.setOnItemClickListener(new RanobeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Manga manga) {
                        // Обработайте нажатие кнопки для выбранного аниме
                        Intent intent = new Intent(getActivity(), RanobeIDActivity.class);

                        intent.putExtra("Id", manga.getId());
                        startActivity(intent);
                    }
                });

                pieceList.setAdapter(adapter);
            }
        });

        ranobeViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.ranobe_filtr_menu, popupMenu.getMenu());

        // Обработчик клика на пункты меню
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Обработка выбора пунктов меню
                switch (item.getItemId()) {
                    case R.id.menu_genres:
                        showDropdownList(adapter, ranobeViewModel.genres);
                        return true;
                    case R.id.menu_status:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_manga_status));
                        return true;
                    case R.id.menu_orders:
                        showDropdownList(adapter, getResources().getStringArray(R.array.spinner_piece_order));
                        return true;
                    case R.id.menu_pages:
                        showDropdownList(adapter, IntStream.rangeClosed(1, 147).toArray());
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

    private void showDropdownList(RanobeAdapter ranobeAdapter, String[] options) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);
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
            switch (ranobeViewModel.getSelectedOrder()) {
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
            switch (ranobeViewModel.getSelectedStatus()) {
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
                            ranobeViewModel.setSelectedOrder("ranked");
                            break;
                        case "По типу":
                            ranobeViewModel.setSelectedOrder("kind");
                            break;
                        case "По популярности":
                            ranobeViewModel.setSelectedOrder("popularity");
                            break;
                        case "По имени":
                            ranobeViewModel.setSelectedOrder("name");
                            break;
                        case "По дате релиза":
                            ranobeViewModel.setSelectedOrder("aired_on");
                            break;
                        case "По статусу":
                            ranobeViewModel.setSelectedOrder("status");
                            break;
                        case "Случайно":
                            ranobeViewModel.setSelectedOrder("random");
                            break;
                    }
                } else if (Arrays.equals(options, getResources().getStringArray(R.array.spinner_manga_status))) {
                    switch (selectedOption) {
                        case "Всё":
                            ranobeViewModel.setSelectedStatus("");
                            break;
                        case "Анонс":
                            ranobeViewModel.setSelectedStatus("anons");
                            break;
                        case "Онгоинг":
                            ranobeViewModel.setSelectedStatus("ongoing");
                            break;
                        case "Вышла":
                            ranobeViewModel.setSelectedStatus("released");
                            break;
                        case "Заморожена":
                            ranobeViewModel.setSelectedStatus("paused");
                            break;
                        case "Остановлена":
                            ranobeViewModel.setSelectedStatus("discontinued");
                            break;
                    }
                }
                ranobeViewModel.getRanobe(ranobeViewModel.getSelectedPage(),
                        ranobeViewModel.getSelectedOrder(),
                        ranobeViewModel.getSelectedStatus(),
                        ranobeViewModel.getSelectedGenre());
                ranobeViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        ranobeAdapter.setRanobeList(listData);
                        ranobeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(RanobeAdapter ranobeAdapter, Genre[] options) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);
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

        int selectedPosition = listId.indexOf(ranobeViewModel.getSelectedGenre());
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
                        ranobeViewModel.setSelectedGenre(listId.get(i));
                    }
                }

                ranobeViewModel.getRanobe(ranobeViewModel.getSelectedPage(),
                        ranobeViewModel.getSelectedOrder(),
                        ranobeViewModel.getSelectedStatus(),
                        ranobeViewModel.getSelectedGenre());
                ranobeViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        ranobeAdapter.setRanobeList(listData);
                        ranobeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(RanobeAdapter ranobeAdapter, int[] options) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);
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

        int selectedPosition = ranobeViewModel.getSelectedPage() - 1;
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
                        ranobeViewModel.setSelectedPage(options[i]);
                    }
                }

                ranobeViewModel.getRanobe(ranobeViewModel.getSelectedPage(),
                        ranobeViewModel.getSelectedOrder(),
                        ranobeViewModel.getSelectedStatus(),
                        ranobeViewModel.getSelectedGenre());
                ranobeViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        ranobeAdapter.setRanobeList(listData);
                        ranobeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    private void showDropdownList(RanobeAdapter ranobeAdapter) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);
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

                ranobeViewModel.getRanobe(enteredText);
                ranobeViewModel.getListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
                    @Override
                    public void onChanged(List<Manga> listData) {
                        // Обновляем адаптер с новыми данными
                        ranobeAdapter.setRanobeList(listData);
                        ranobeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

}
