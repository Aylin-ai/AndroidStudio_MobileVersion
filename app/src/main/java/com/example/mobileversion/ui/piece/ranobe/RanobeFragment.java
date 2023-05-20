package com.example.mobileversion.ui.piece.ranobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileversion.databinding.FragmentRanobeBinding;

public class RanobeFragment extends Fragment {

    private FragmentRanobeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RanobeViewModel ranobeViewModel =
                new ViewModelProvider(this).get(RanobeViewModel.class);

        binding = FragmentRanobeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
