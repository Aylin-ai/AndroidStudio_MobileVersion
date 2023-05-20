package com.example.mobileversion.ui.piece.ranobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.mobileversion.ui.piece.manga.MangaViewModel;

import java.util.List;

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


        ranobeViewModel.getManga(1, "ranked", "", "", 0);
        ranobeViewModel.getMangaListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Manga>>() {
            @Override
            public void onChanged(List<Manga> ranobes) {
                adapter = new RanobeAdapter(ranobes);
                pieceList.setAdapter(adapter);
            }
        });

        ranobeViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
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
