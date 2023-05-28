package com.example.mobileversion.ui.userlist.ranobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentUserMangaBinding;
import com.example.mobileversion.databinding.FragmentUserRanobeBinding;
import com.example.mobileversion.ui.piece.mangaID.MangaIDActivity;
import com.example.mobileversion.ui.piece.ranobeID.RanobeIDActivity;
import com.example.mobileversion.ui.userlist.manga.UserMangaViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import models.Manga;
import models.MangaAdapter;
import models.RanobeAdapter;

public class UserRanobeFragment extends Fragment {
    private RecyclerView userRanobeList;
    private RanobeAdapter ranobeAdapter;
    private FragmentUserRanobeBinding binding;
    private FloatingActionButton userListButton;
    private UserRanobeViewModel userViewModel; //

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserRanobeViewModel.class); // Инициализируем ViewModel

        binding = FragmentUserRanobeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userRanobeList = root.findViewById(R.id.userRanobeList);
        userRanobeList.setLayoutManager(new LinearLayoutManager(getContext()));
        userListButton = root.findViewById(R.id.userListButton);

        userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Выберите опцию");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.spinner_manga_list));

                // Создание Spinner и установка адаптера
                final Spinner spinner = new Spinner(getActivity());
                spinner.setAdapter(adapter);

                builder.setView(spinner);

                // Установка кнопок "OK" и "Отмена"
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption = (String) spinner.getSelectedItem();
                        userViewModel.loadRanobeFromDatabase(selectedOption);
                    }
                });
                builder.setNegativeButton("Отмена", null);

                // Отображение диалогового окна
                builder.show();
            }
        });

        // Наблюдаем за изменениями в LiveData animeListLiveData
        userViewModel.getRanobeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
            @Override
            public void onChanged(List<Manga> ranobeList) {
                if (ranobeList == null || ranobeList.isEmpty()) {
                    // Список аниме пуст или равен null, скрываем RecyclerView
                    userRanobeList.setVisibility(View.GONE);
                } else {
                    ranobeAdapter = new RanobeAdapter(ranobeList, getActivity());
                    ranobeAdapter.setOnItemClickListener(new RanobeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Manga manga) {
                            // Обработайте нажатие кнопки для выбранного аниме
                            Intent intent = new Intent(getActivity(), RanobeIDActivity.class);
                            intent.putExtra("Id", manga.getId());
                            startActivity(intent);
                        }
                    });
                    userRanobeList.setAdapter(ranobeAdapter);
                    // Показываем RecyclerView
                    userRanobeList.setVisibility(View.VISIBLE);
                }
            }
        });

        return root;
    }
}