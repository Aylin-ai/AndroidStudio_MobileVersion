package com.example.mobileversion.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mobileversion.AppShellActivity;
import com.example.mobileversion.MainActivity;
import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ImageView userImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private EditText userNewNameEditText;
    private EditText userNewPassword1EditText;
    private EditText userNewPassword2EditText;
    private Button userSubmitButton;
    private Button userDeleteAccButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userImageView = binding.userImageView;
        userNameTextView = binding.userNameTextView;
        userEmailTextView = binding.userEmailTextView;
        userNewNameEditText = binding.userNewNameEditText;
        userNewPassword1EditText = binding.userNewPassword1EditText;
        userNewPassword2EditText = binding.userNewPassword2EditText;
        userSubmitButton = binding.userSubmitButton;
        userDeleteAccButton = binding.userDeleteAccButton;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photo = user.getPhotoUrl();

        Glide.with(this)
                .load(photo)
                .into(userImageView);
        userNameTextView.setText(name);
        userEmailTextView.setText(email);

        userSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!userNewNameEditText.getText().toString().equals("")) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userNewNameEditText.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Имя изменено успешно.",
                                                    Toast.LENGTH_SHORT).show();
                                            userNameTextView.setText(user.getDisplayName());
                                        }
                                    }
                                });
                    }
                    if (!(userNewPassword1EditText.getText().toString().equals("") ||
                            userNewPassword2EditText.getText().toString().equals(""))) {
                        if (userNewPassword1EditText.getText().toString()
                                .equals(userNewPassword2EditText.getText().toString())) {
                            user.updatePassword(userNewPassword1EditText.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Пароль изменен успешно.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                } catch (Exception ex) {
                    String message = ex.getMessage();
                }
            }
        });
        userDeleteAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
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
