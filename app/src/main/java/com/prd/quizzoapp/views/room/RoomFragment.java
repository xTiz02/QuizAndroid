package com.prd.quizzoapp.views.room;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentRoomBinding;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.util.Data;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;
import com.prd.quizzoapp.views.quiz.QuizActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


public class RoomFragment extends Fragment {

    private UserAdapter userAdapter;
    private FragmentRoomBinding binding;
    private RoomService rS;
    private String idRoom;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    public RoomFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRoomBinding.bind(view);
        idRoom = DataSharedPreference.getData(Util.ROOM_UUID_KEY, getContext());
        rS = new RoomService(getContext());
        DatabaseReference dbRoomRef = db.getReference("rooms").child(idRoom);
        userAdapter = new UserAdapter(new ArrayList<>());
        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUsers.setAdapter(userAdapter);

        //Copiar en portapapeles el código de la sala
        binding.btnCopy.setOnClickListener(v -> {
            String roomCode = binding.tvCode.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Room Code", roomCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Código de la sala copiado", Toast.LENGTH_SHORT).show();
        });
        binding.btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QuizActivity.class);
            //enviar una lista de preguntas al QuizActivity
            //Cada jugador estara esperando las preguntas del webSocket
             intent.putExtra("questions", Data.getQuestions());
            startActivity(intent);
        });

        binding.roomConfig.setOnClickListener(v -> {
            //Cargar CreateRoomFragment en el contenedor del main activity
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_roomFragment_to_createRoomFragment);
        });

        //obtener la lista de usuarios de la sala
       /* dbRoomRef.child("usersRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        UserRoom userRoom = userSnapshot.getValue(UserRoom.class);
                        System.out.println("Usuario inicial en la sala: " + userRoom);
                        userAdapter.addUser(userRoom); // Añadir a la lista inicial
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener los usuarios de la sala", Toast.LENGTH_SHORT).show();
            }
        });*/

        //Obtener conf de la sala
        dbRoomRef.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    System.out.println("Se obtuvo la conf de la sala: " + snapshot.getValue());
                    binding.tvCode.setText(snapshot.child("roomConfig").child("code").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener la sala", Toast.LENGTH_SHORT).show();
            }
        });

        //Obtener los usuarios de la sala
        dbRoomRef.child("usersRoom").orderByChild("admin").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                UserRoom userRoom = snapshot.getValue(UserRoom.class);
                System.out.println("UserRoom que se agrego "+userRoom);
                userAdapter.addUser(userRoom);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                UserRoom userRoom = snapshot.getValue(UserRoom.class);
                System.out.println("UserRoom que se actualizo "+userRoom);
                userAdapter.updateUser(userRoom);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                UserRoom userRoom = snapshot.getValue(UserRoom.class);
                System.out.println("UserRoom que se elimino "+userRoom);
                userAdapter.removeUser(userRoom.getUUID());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                System.out.println("UserRoom que se movio "+snapshot.getValue(UserRoom.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener evento de los usuarios", Toast.LENGTH_SHORT).show();
            }
        });

    }
}