package models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PieceRepository {
    public void loadAnimeFromDatabase(DataLoadCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("anime");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PieceInUserList> pieces = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    PieceInUserList piece = childSnapshot.getValue(PieceInUserList.class);
                    pieces.add(piece);
                }

                callback.onDataLoaded(pieces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    public void loadMangaFromDatabase(DataLoadCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("manga");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PieceInUserList> pieces = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    PieceInUserList piece = childSnapshot.getValue(PieceInUserList.class);
                    pieces.add(piece);
                }

                callback.onDataLoaded(pieces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    public void loadRanobeFromDatabase(DataLoadCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ranobe");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PieceInUserList> pieces = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    PieceInUserList piece = childSnapshot.getValue(PieceInUserList.class);
                    pieces.add(piece);
                }

                callback.onDataLoaded(pieces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
            }
        });
    }
    public interface DataLoadCallback {
        void onDataLoaded(List<PieceInUserList> pieces);
    }
    public interface PieceDataLoadCallback {
        void onDataLoaded(PieceInUserList piece);
    }
}

