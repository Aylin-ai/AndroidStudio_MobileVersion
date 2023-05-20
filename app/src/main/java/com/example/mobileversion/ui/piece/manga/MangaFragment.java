package com.example.mobileversion.ui.piece.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileversion.databinding.FragmentMangaBinding;
import com.example.mobileversion.ui.piece.manga.MangaViewModel;

public class MangaFragment extends Fragment {

    private FragmentMangaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MangaViewModel mangaViewModel =
                new ViewModelProvider(this).get(MangaViewModel.class);

        binding = FragmentMangaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
