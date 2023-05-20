package com.example.mobileversion.ui.piece.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentMangaBinding;

import java.util.List;

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


        mangaViewModel.getManga(1, "ranked", "", "", 0);
        mangaViewModel.getMangaListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
            @Override
            public void onChanged(List<Manga> mangas) {
                adapter = new MangaAdapter(mangas);
                pieceList.setAdapter(adapter);
            }
        });

        mangaViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
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
