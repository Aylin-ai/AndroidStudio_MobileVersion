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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.PieceInUserList;
import models.User;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ImageView userImageView;
    private TextView userNameTextView;
    private TextView userRoleTextView;
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
        userRoleTextView = binding.userRoleTextView;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        userRoleTextView.setText(user.getRole());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        Uri photo = firebaseUser.getPhotoUrl();

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
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Имя изменено успешно.",
                                                    Toast.LENGTH_SHORT).show();
                                            userNameTextView.setText(firebaseUser.getDisplayName());
                                        }
                                    }
                                });
                    }
                    if (!(userNewPassword1EditText.getText().toString().equals("") ||
                            userNewPassword2EditText.getText().toString().equals(""))) {
                        if (userNewPassword1EditText.getText().toString()
                                .equals(userNewPassword2EditText.getText().toString())) {
                            firebaseUser.updatePassword(userNewPassword1EditText.getText().toString())
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
