package com.example.mobileversion.ui.piece.anime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentAnimeBinding;

import java.util.ArrayList;
import java.util.List;

import models.Anime;
import models.AnimeAdapter;

public class AnimeFragment extends Fragment {
    private RecyclerView animeList;
    private List<Anime> animeListData;
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


        animeViewModel.getAnimes(1, "ranked", "", "", 0);
        animeViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
            @Override
            public void onChanged(List<Anime> animeListData) {
                // Обновляем адаптер с новыми данными
                adapter = new AnimeAdapter(animeListData);
                animeList.setAdapter(adapter);
            }
        });

        animeViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                // Обрабатываем сообщение
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
