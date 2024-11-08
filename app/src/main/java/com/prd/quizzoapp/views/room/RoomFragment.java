package com.prd.quizzoapp.views.room;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.entity.Room;
import com.prd.quizzoapp.model.entity.RoomConfig;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.QuizServerImpl;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.QuizServerService;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class RoomFragment extends Fragment {

    private UserAdapter userAdapter;
    private FragmentRoomBinding binding;
    private RoomService rS;
    private LoadingService ls;
    private QuizServerService qs;
    private String idRoom;
    private boolean isAdmin = false;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private QuizRequestDto quizRequestDto;

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
        qs = new QuizServerImpl();
        rS = new RoomService(getContext());
        DatabaseReference dbRoomRef = db.getReference("rooms").child(idRoom);
        userAdapter = new UserAdapter(new ArrayList<>());
        ls = new LoadingService(getContext());
        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUsers.setAdapter(userAdapter);

        binding.btnCopy.setOnClickListener(v -> {
            String roomCode = binding.tvCode.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Room Code", roomCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Código de la sala copiado", Toast.LENGTH_SHORT).show();
        });

        binding.roomConfig.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_roomFragment_to_createRoomFragment);
        });

        binding.btnLeaveRoom.setOnClickListener(v -> {
            ls.leaveToRoomDialog(()->{
                rS.deleteRoom(idRoom, auth.getUid().toString(), isAdmin, new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        if(!isAdmin){
                            navToHome();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Error al dejar la sala", Toast.LENGTH_SHORT).show();
                    }
                });
            },isAdmin);
        });

        binding.btnStartGame.setOnClickListener(v -> {
            ls.showLoading("Generando preguntas ...");
            qs.generateQuestions(quizRequestDto,idRoom, new ActionCallback() {
                @Override
                public void onSuccess() {
                    ls.hideLoading();
                }

                @Override
                public void onFailure(Exception e) {
                    ls.hideLoading();
                    Toast.makeText(getContext(), "Error al generar preguntas", Toast.LENGTH_SHORT).show();
                }
            });
        });

        //Conectar al servidor de eventos
        SseManager.getInstance().connect(idRoom);
        /*qs.connectToSseServer(idRoom, new DataActionCallback<String>() {
            @Override
            public void onSuccess(String data) {
                receive = data;
                System.out.println("Data recibida en el activity: "+receive);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });*/



        //Obtener conf de la sala
        dbRoomRef.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    System.out.println("Se obtuvo la conf de la sala: " + snapshot.getValue());
                    Room room = snapshot.getValue(Room.class);
                    RoomConfig roomConfig = room.getRoomConfig();
                    binding.tvCode.setText(roomConfig.getCode());
                    if(auth.getUid().toString().equals(roomConfig.getUuidAdmin())){
                        isAdmin = true;
                        binding.btnStartGame.setVisibility(View.VISIBLE);
                        binding.btnLeaveRoom.setVisibility(View.VISIBLE);
                        binding.roomConfig.setVisibility(View.VISIBLE);
                    }else{
                        isAdmin = false;
                        binding.btnLeaveRoom.setVisibility(View.VISIBLE);
                    }
                    quizRequestDto = new QuizRequestDto(
                            roomConfig.getQuestions(),
                            room.getCategories().stream()
                                    .map(category -> category.getCategory().getName())
                                    .collect(Collectors.toCollection(ArrayList::new)),
                            room.getSubCategories());
                }else {
                    DataSharedPreference.removeData(Util.ROOM_UUID_KEY, getContext());
                    navToHome();
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

    private void navToHome(){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.navigate(R.id.homeFragment);
    }


}