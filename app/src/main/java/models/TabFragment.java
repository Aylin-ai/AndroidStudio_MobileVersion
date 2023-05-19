package models;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobileversion.R;

public class TabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Создание и настройка макета фрагмента
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        // Настройка содержимого вкладки
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(getArguments().getString("content"));

        return view;
    }
}
