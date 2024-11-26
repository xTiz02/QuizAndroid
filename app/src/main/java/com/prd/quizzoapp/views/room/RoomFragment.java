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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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
import com.prd.quizzoapp.model.entity.UserResult;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.QuizServerImpl;
import com.prd.quizzoapp.model.service.ResultService;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.QuizServerService;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;
import com.prd.quizzoapp.views.chat.BottomChatView;
import com.prd.quizzoapp.views.quiz.LeaderBoardActivity;

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
    private boolean isAdmin;
    private FirebaseAuth auth;
    private QuizRequestDto quizRequestDto;
    private ResultService resultService;
    private DatabaseReference dbRoomConf, dbResults, dbRoom, dbRoomUsers;
    private ValueEventListener evlConfig;
    private ChildEventListener cvlUsers;

    public RoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idRoom = DataSharedPreference.getData(Util.ROOM_UUID_KEY, getContext());
        isAdmin = DataSharedPreference.getBooleanData(Util.IS_ADMIN_KEY, getContext());
        dbRoom = FirebaseDatabase.getInstance().getReference("rooms").child(idRoom);
        dbResults = FirebaseDatabase.getInstance().getReference("results").child(idRoom).child("users");

        initServices();
    }
    //cuando se presiona el boton de regresar


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Util.showLog("RoomFragment", "Se presiono el boton de regresar");
            }
        });
        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUsers.setAdapter(userAdapter);

        rS.changePlayingState(
                DataSharedPreference.getData(Util.ROOM_UUID_KEY, getContext()),
                auth.getUid().toString(),
                false);

        binding.btnChat.setOnClickListener(v -> {
            BottomChatView chatView = new BottomChatView();
            chatView.show(getActivity().getSupportFragmentManager(), chatView.getTag());
        });
        binding.btnResults.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LeaderBoardActivity.class);
            startActivity(intent);
        });
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
            ArrayList<UserRoom> usersInRoom = userAdapter.getUsers();
            dbResults.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            UserResult user = userSnapshot.getValue(UserResult.class);
                            if(usersInRoom.stream().noneMatch(userRoom -> userRoom.getUUID().equals(user.getUserUuid()))){
                                resultService.deleteUserResult(idRoom, user.getUserUuid(), new ActionCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Util.showLog("RoomFragment", "Resultados de usuario inactivo eliminados1: " + user.getUserUuid());
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Util.showLog("RoomFragment", "Error al eliminar resultados de usuario: " + user.getUserUuid());
                                    }
                                });
                            }
                        }
                        ArrayList<UserRoom> usersPlaying = userAdapter.getUsers().stream()
                                .filter(userRoom -> userRoom.isPlaying())
                                .collect(Collectors.toCollection(ArrayList::new));
                        if(!usersPlaying.isEmpty()){
                            Util.showLog("RoomFragment", "Usuarios inactivos: "+usersPlaying);
                            for (UserRoom user : usersPlaying) {
                                resultService.deleteUserResult(idRoom, user.getUUID(), new ActionCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Util.showLog("RoomFragment", "Resultados de usuario inactivo eliminados2" + user.getUUID());
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Util.showLog("RoomFragment", "Error al eliminar resultados de usuario:" + user.getUUID());
                                    }
                                });
                            }
                        }else {
                            Util.showLog("RoomFragment", "No hay usuarios inactivos");
                        }
                        qs.generateQuestions(quizRequestDto,idRoom, new ActionCallback() {
                            @Override
                            public void onSuccess() {
                                ls.hideLoading();
                                Util.showLog("RoomFragment", "Preguntas generadas");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ls.hideLoading();
                                Toast.makeText(getContext(), "Error al generar preguntas", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        Util.showLog("RoomFragment", "No hay resultados de usuarios");
                        ls.hideLoading();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                           /* resultService.deleteAllUserResults(idRoom, new ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    Util.showLog("RoomFragment", "Resultados de usuarios eliminados");
                                    ls.hideLoading();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Util.showLog("RoomFragment", "Error al eliminar resultados de usuarios");
                                    ls.hideLoading();
                                }
                            });*/
                    Util.showLog("RoomFragment", "Error al obtener resultados de usuarios");
                    ls.hideLoading();
                }
            });
        });

        //Cambiar estado de isPlaying a false
        dbRoom.child("usersRoom").child(auth.getUid().toString()).child("playing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   boolean isPlaying = snapshot.getValue(Boolean.class);
                     if(isPlaying) {
                         rS.changePlayingState(
                                    idRoom,
                                    auth.getUid().toString(),
                                    false
                         );
                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showLog("RoomFragment", "Error al obtener el estado de isPlaying");
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListener();
    }

    public void initListener() {
        if(evlConfig==null){
            Util.showLog("RoomFragment", "Se agrego el evlConfig listener");
            evlConfig = new ValueEventListener() {
                //Obtener conf de la sala
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) {
                        // Si el fragmento ya no está adjunto, no proceses la respuesta
                        return;
                    }

                    if (snapshot.exists()) {
                        SseManager.getInstance().connect(idRoom);
                        Util.showLog("RoomFragment", "Configuración de la sala obtenida");
                        Room room = snapshot.getValue(Room.class);
                        RoomConfig roomConfig = room.getRoomConfig();
                        binding.tvCode.setText(roomConfig.getCode());
                        if (isAdmin) {
                            binding.btnStartGame.setVisibility(View.VISIBLE);
                            binding.btnLeaveRoom.setVisibility(View.VISIBLE);
                            binding.roomConfig.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnLeaveRoom.setVisibility(View.VISIBLE);
                        }
                        quizRequestDto = new QuizRequestDto(
                                roomConfig.getQuestions(),
                                room.getCategories().stream()
                                        .map(category -> category.getCategory().getName())
                                        .collect(Collectors.toCollection(ArrayList::new)),
                                room.getSubCategories()
                        );
                    } else {
                        if (isAdded()) { // Asegúrate de que el fragment aún está adjunto antes de llamar a requireContext()
                            DataSharedPreference.clearData(requireContext());
                            Util.showLog("RoomFragment", "Se elimianron los shared preferences de la sala");
                            navToNotRoom();
                            Toast.makeText(requireContext(), "La sala ya no existe", Toast.LENGTH_LONG).show();
                            SseManager.getInstance().disconnect();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog("RoomFragment", "Error al obtener la configuración de la sala");
                }

            };
            dbRoomConf = dbRoom.child("settings");
            dbRoomConf.addValueEventListener(evlConfig);
        }

        if(cvlUsers==null){
            Util.showLog("RoomFragment", "Se agrego el cvlUsers listener");
            cvlUsers = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    UserRoom userRoom = snapshot.getValue(UserRoom.class);
                    Util.getImages().put(userRoom.getUUID(), userRoom.getImg());
                    userAdapter.addUser(userRoom);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    UserRoom userRoom = snapshot.getValue(UserRoom.class);
                    userAdapter.updateUser(userRoom);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    UserRoom userRoom = snapshot.getValue(UserRoom.class);
                    userAdapter.removeUser(userRoom.getUUID());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog("RoomFragment", "Error al obtener los usuarios de la sala");
                }
            };
            dbRoomUsers = dbRoom.child("usersRoom");
            dbRoomUsers.orderByChild("admin").addChildEventListener(cvlUsers);
        }


        }


    public void initServices() {
        auth = FirebaseAuth.getInstance();
        qs = new QuizServerImpl();
        rS = new RoomService(getContext());
        ls = new LoadingService(getContext());
        resultService = new ResultService(getContext());
        userAdapter = new UserAdapter(new ArrayList<>(), getContext());
    }

    public void removeListener() {
        userAdapter.clearUsers();
        if(evlConfig!=null){
            Util.showLog("RoomFragment", "Se removio el  evlConfig listener");
            dbRoomConf.removeEventListener(evlConfig);
            evlConfig = null;
        }
        if(cvlUsers!=null){
            Util.showLog("RoomFragment", "Se removio el cvlUsers listener");
            dbRoomUsers.removeEventListener(cvlUsers);
            cvlUsers = null;

        }
    }

    private void navToHome(){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.navigate(R.id.homeFragment, null, new NavOptions.Builder()
                .setPopUpTo(navController.getGraph().getStartDestinationId(), true) // Limpia la pila
                .build());
    }

    private void navToNotRoom(){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.navigate(R.id.notRoomFragment);
    }


}