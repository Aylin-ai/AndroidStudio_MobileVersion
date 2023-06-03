package models;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileversion.AppShellActivity;
import com.example.mobileversion.MainActivity;
import com.example.mobileversion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class RegistrationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Дополнительная логика для фрагмента регистрации

        EditText userPassword1 = view.findViewById(R.id.userPassword1);
        EditText userPassword2 = view.findViewById(R.id.userPassword2);
        EditText userEmail = view.findViewById(R.id.userEmail);
        EditText userName = view.findViewById(R.id.userName);

        Button registrationButton = view.findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password1 = userPassword1.getText().toString();
                String Password2 = userPassword2.getText().toString();
                String Email = userEmail.getText().toString();
                String Name = userName.getText().toString();
                if (Password1.equals("") ||
                Password2.equals("") || Email.equals("") || Name.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Ошибка")
                            .setMessage("Введены не все значения");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if (!Password1.equals(Password2)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Ошибка")
                            .setMessage("Пароли не совпадают");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, Password1)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(Name)
                                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/antrap-firebase.appspot.com/o/OldPif.jpg?alt=media&token=6b117022-e75e-4b3c-b859-937f89516f8b"))
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(profileUpdateTask -> {
                                                if (profileUpdateTask.isSuccessful()) {
                                                    Intent intent = new Intent(getContext(), AppShellActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(getContext(), "Регистрация прошла успешно.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    User newUser = new User();
                                    newUser.setEmail(user.getEmail());
                                    newUser.setId(user.getUid());
                                    newUser.setImageSource("https://firebasestorage.googleapis.com/v0/b/antrap-firebase.appspot.com/o/OldPif.jpg?alt=media&token=6b117022-e75e-4b3c-b859-937f89516f8b");
                                    newUser.setLogin(Name);
                                    newUser.setRole("Пользователь");

                                    // Получите ссылку на узел, где хранятся данные для аниме
                                    DatabaseReference animeRef = database.getReference("users")
                                            .child(String.format("%s",
                                                    user.getEmail()
                                                            .replace('.', ',')));

                                    // Добавьте данные аниме в базу данных
                                    animeRef.setValue(newUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Успешно добавлено в базу данных
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Обработка ошибки при добавлении в базу данных
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(getContext(), "Ошибка.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }
}

