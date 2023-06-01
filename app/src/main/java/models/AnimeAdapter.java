package models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Anime anime);
    }
    FirebaseUser user;
    private Context context;
    private List<Anime> animeList;
    private OnItemClickListener itemClickListener;
    private List<PieceInUserList> pieces;
    private PieceRepository pieceRepository;
    private boolean isFirstLoad;
    private boolean isDataLoaded;
    public AnimeAdapter(List<Anime> animeList, Context context) {
        try {
            this.context = context;
            this.animeList = animeList;
            this.pieces = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            pieceRepository = new PieceRepository();
            this.isFirstLoad = true;
            pieceRepository.loadAnimeFromDatabase(new PieceRepository.DataLoadCallback() {
                @Override
                public void onDataLoaded(List<PieceInUserList> pieces) {
                    AnimeAdapter.this.pieces = pieces;
                    notifyDataSetChanged();
                    isDataLoaded = true;
                }
            });
        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }
    public void setAnimeList(List<Anime> animeList){
        this.animeList = animeList;
        notifyDataSetChanged();
    }
    private PieceInUserList findPieceForAnime(Anime anime) {
        for (PieceInUserList piece : pieces) {
            if (piece.getPieceId() == anime.getId()) {
                return piece;
            }
        }
        return null;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new ViewHolder(view, itemClickListener, animeList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        // Bind data to the views in the item layout
        Glide.with(holder.itemView)
                .load(anime.getImage().getOriginal())
                .into(holder.pieceImage);
        holder.pieceRussianName.setText(anime.getRussian());
        holder.pieceEnglishName.setText(anime.getName());
        holder.pieceKind.setText(anime.getKind());
        holder.pieceStatus.setText(anime.getStatus());
        holder.pieceScore.setText(anime.getScore());

        // Set click listener for the button
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && itemClickListener != null) {
                    Anime clickedAnime = animeList.get(adapterPosition);
                    itemClickListener.onItemClick(clickedAnime);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.spinner_anime_list,
                android.R.layout.simple_spinner_item
        );

        if (isDataLoaded) {
            PieceInUserList piece = findPieceForAnime(anime);
            if (piece != null) {
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.userListSpinner.setAdapter(adapter);
                int positionInSpinner = adapter.getPosition(piece.getUserList());
                holder.userListSpinner.setSelection(positionInSpinner);
            } else {
                holder.userListSpinner.setSelection(adapter.getPosition("Нет"));
            }

            // Enable the spinner and set the listener
            holder.userListSpinner.setEnabled(true);
            holder.userListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedValue = parent.getItemAtPosition(position).toString();
                    Anime anime = animeList.get(holder.getAdapterPosition());
                    if (selectedValue.equals("Нет")) {
                        deletePieceFromDatabase(anime);
                    } else {
                        PieceInUserList piece = findPieceForAnime(anime);
                        if (piece != null) {
                            updatePieceInDatabase(piece, selectedValue);
                        } else {
                            addPieceToDatabase(anime, selectedValue);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Обработка ситуации, когда не выбран ни один элемент
                }
            });
        } else {
            // Data is not loaded yet, disable the spinner
            holder.userListSpinner.setEnabled(false);
        }
    }


    @Override
    public int getItemCount() {
        return animeList.size();
    }
    private void deletePieceFromDatabase(Anime anime) {
        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("anime")
                .child(String.format("%d %s", anime.getId(),
                        user.getEmail().replace('.', ',')));

        // Удалите данные аниме из базы данных
        animeRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Успешно удалено из базы данных
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Обработка ошибки при удалении из базы данных
                    }
                });
    }

    private void updatePieceInDatabase(PieceInUserList piece, String selectedValue) {
        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("anime")
                .child(String.format("%d %s", piece.getPieceId(),
                        user.getEmail().replace('.', ',')));

        // Обновите значение списка в базе данных
        piece.setUserList(selectedValue);
        animeRef.setValue(piece)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Успешно обновлено в базе данных
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Обработка ошибки при обновлении в базе данных
                    }
                });
    }

    private void addPieceToDatabase(Anime anime, String selectedValue) {
        // Создайте новый объект PieceInUserList для добавления в базу данных
        PieceInUserList piece = new PieceInUserList();
        piece.setPieceId(anime.getId());
        piece.setUserEmail(user.getEmail());
        piece.setUserList(selectedValue);

        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("anime")
                .child(String.format("%d %s", anime.getId(),
                        user.getEmail().replace('.', ',')));

        // Добавьте данные аниме в базу данных
        animeRef.setValue(piece)
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pieceImage;
        Spinner userListSpinner;
        TextView pieceRussianName;
        TextView pieceEnglishName;
        TextView pieceKind;
        TextView pieceStatus;
        TextView pieceScore;
        Button detailsButton;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener, List<Anime> animeList) {
            super(itemView);
            pieceImage = itemView.findViewById(R.id.pieceImage);
            userListSpinner = itemView.findViewById(R.id.userListSpinner);
            pieceRussianName = itemView.findViewById(R.id.pieceRussianName);
            pieceEnglishName = itemView.findViewById(R.id.pieceEnglishName);
            pieceKind = itemView.findViewById(R.id.pieceKind);
            pieceStatus = itemView.findViewById(R.id.pieceStatus);
            pieceScore = itemView.findViewById(R.id.pieceScore);
            detailsButton = itemView.findViewById(R.id.detailsButton);

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Anime anime = animeList.get(position);
                        listener.onItemClick(anime);
                    }
                }
            });
        }
    }
}
