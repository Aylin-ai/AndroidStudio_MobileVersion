package com.example.mobileversion.ui.userlist.anime;

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
import com.example.mobileversion.databinding.FragmentAnimeBinding;
import com.example.mobileversion.databinding.FragmentUserAnimeBinding;
import com.example.mobileversion.ui.piece.anime.AnimeViewModel;
import com.example.mobileversion.ui.piece.animeID.AnimeIDActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Anime;
import models.AnimeAdapter;
import models.AnimeID;
import models.PieceInUserList;

public class UserAnimeFragment extends Fragment {
    private RecyclerView userAnimeList;
    private AnimeAdapter animeAdapter;
    private FragmentUserAnimeBinding binding;
    private FloatingActionButton userListButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserAnimeViewModel userAnimeViewModel =
                new ViewModelProvider(this).get(UserAnimeViewModel.class);

        binding = FragmentUserAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userAnimeList = root.findViewById(R.id.userAnimeList);
        userAnimeList.setLayoutManager(new LinearLayoutManager(getContext()));
        userListButton = root.findViewById(R.id.userListButton);

        userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Выберите опцию");


                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.spinner_anime_list));

                // Создание Spinner и установка адаптера
                final Spinner spinner = new Spinner(getActivity());
                spinner.setAdapter(adapter);

                builder.setView(spinner);

                // Установка кнопок "OK" и "Отмена"
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption = (String) spinner.getSelectedItem();
                        userAnimeViewModel.loadAnimeFromDatabase(selectedOption);
                        userAnimeViewModel.getPiecesListLiveData().
                                observe(getViewLifecycleOwner(), new Observer<List<PieceInUserList>>() {
                            @Override
                            public void onChanged(List<PieceInUserList> pieceInUserLists) {
                                userAnimeViewModel.getAnimes(pieceInUserLists);
                                userAnimeViewModel.getAnimeListLiveData()
                                        .observe(getViewLifecycleOwner(), new Observer<List<AnimeID>>() {
                                            @Override
                                            public void onChanged(List<AnimeID> animeIDS) {
                                                List<Anime> animeList = new ArrayList<>();
                                                for (AnimeID animeID: animeIDS) {
                                                    Anime anime = new Anime();
                                                    anime.setId(animeID.getId());
                                                    anime.setImage(animeID.getImage());
                                                    anime.setKind(animeID.getKind());
                                                    anime.setName(animeID.getName());
                                                    anime.setRussian(animeID.getRussian());
                                                    anime.setEpisodes(animeID.getEpisodes());
                                                    anime.setEpisodesAired(animeID.getEpisodesAired());
                                                    anime.setAiredOn(animeID.getAiredOn());
                                                    anime.setScore(animeID.getScore());
                                                    anime.setStatus(animeID.getStatus());
                                                    anime.setReleasedOn(animeID.getReleasedOn());
                                                    animeList.add(anime);
                                                }
                                                animeAdapter = new AnimeAdapter(animeList, getActivity());
                                                animeAdapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(Anime anime) {
                                                        // Обработайте нажатие кнопки для выбранного аниме
                                                        Intent intent = new Intent(getActivity(), AnimeIDActivity.class);

                                                        intent.putExtra("Id", anime.getId());
                                                        startActivity(intent);
                                                    }
                                                });
                                                userAnimeList.setAdapter(animeAdapter);
                                            }
                                        });
                            }
                        });
                    }
                });
                builder.setNegativeButton("Отмена", null);

                // Отображение диалогового окна
                builder.show();
            }
        });

        return root;
    }
}
