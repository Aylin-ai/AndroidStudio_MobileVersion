package models;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileversion.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RanobeAdapter extends RecyclerView.Adapter<RanobeAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Manga manga);
    }
    FirebaseUser user;
    private Context context;
    private List<Manga> ranobeList;
    private RanobeAdapter.OnItemClickListener itemClickListener;
    private List<PieceInUserList> pieces;
    private PieceRepository pieceRepository;
    private boolean isDataLoaded;

    public RanobeAdapter(List<Manga> ranobeList, Context context) {
        try {
            this.context = context;
            this.ranobeList = ranobeList;
            this.pieces = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            pieceRepository = new PieceRepository();
            pieceRepository.loadRanobeFromDatabase(new PieceRepository.DataLoadCallback() {
                @Override
                public void onDataLoaded(List<PieceInUserList> pieces) {
                    RanobeAdapter.this.pieces = pieces;
                    notifyDataSetChanged();
                    isDataLoaded = true;
                }
            });
        } catch (Exception ex) {
            String message = ex.getMessage();
        }
    }

    public void setOnItemClickListener(RanobeAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manga, parent, false);
        return new RanobeAdapter.ViewHolder(view, itemClickListener, ranobeList);
    }

    public void setRanobeList(List<Manga> ranobeList){
        this.ranobeList = ranobeList;
        notifyDataSetChanged();
    }
    private PieceInUserList findPieceForRanobe(Manga ranobe) {
        for (PieceInUserList piece : pieces) {
            if (piece.getPieceId() == ranobe.getId()) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manga ranobe = ranobeList.get(position);

        // Bind data to the views in the item layout
        Glide.with(holder.itemView)
                .load(ranobe.getImage().getOriginal())
                .into(holder.pieceImage);
        holder.pieceRussianName.setText(ranobe.getRussian());
        holder.pieceEnglishName.setText(ranobe.getName());
        holder.pieceKind.setText(ranobe.getKind());
        holder.pieceStatus.setText(ranobe.getStatus());
        holder.pieceScore.setText(ranobe.getScore());

        // Set click listener for the button
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && itemClickListener != null) {
                    Manga clickedRanobe = ranobeList.get(adapterPosition);
                    itemClickListener.onItemClick(clickedRanobe);
                }
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.spinner_manga_list,
                android.R.layout.simple_spinner_item
        );

        if (isDataLoaded) {
            PieceInUserList piece = findPieceForRanobe(ranobe);
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
                    Manga ranobe = ranobeList.get(holder.getAdapterPosition());
                    if (selectedValue.equals("Нет")) {
                        deletePieceFromDatabase(ranobe);
                    } else {
                        PieceInUserList piece = findPieceForRanobe(ranobe);
                        if (piece != null) {
                            updatePieceInDatabase(piece, selectedValue);
                        } else {
                            addPieceToDatabase(ranobe, selectedValue);
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
        return ranobeList.size();
    }
    private void deletePieceFromDatabase(Manga ranobe) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference animeRef = database.getReference("ranobe").child(String.valueOf(ranobe.getId()));
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
        DatabaseReference animeRef = database.getReference("ranobe").child(String.valueOf(piece.getPieceId()));

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

    private void addPieceToDatabase(Manga ranobe, String selectedValue) {
        // Создайте новый объект PieceInUserList для добавления в базу данных
        PieceInUserList piece = new PieceInUserList();
        piece.setPieceId(ranobe.getId());
        piece.setUserEmail(user.getEmail());
        piece.setUserList(selectedValue);

        // Получите ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Получите ссылку на узел, где хранятся данные для аниме
        DatabaseReference animeRef = database.getReference("ranobe").child(String.valueOf(ranobe.getId()));

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

        public ViewHolder(@NonNull View itemView, RanobeAdapter.OnItemClickListener listener, List<Manga> ranobeList) {
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
                        Manga ranobe = ranobeList.get(position);
                        listener.onItemClick(ranobe);
                    }
                }
            });
        }
    }
}