package com.prd.quizzoapp.views.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prd.quizzoapp.databinding.ChatViewBinding;
import com.prd.quizzoapp.model.entity.UserMessage;
import com.prd.quizzoapp.model.service.ChatService;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class BottomChatView extends BottomSheetDialogFragment {

    private MessageAdapter msgAdapter;
    private ChatService chatService;
    private DatabaseReference dbRef;
    private String idRoom;
    private ValueEventListener velChat;
    private ChatViewBinding binding;

    public BottomChatView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idRoom = DataSharedPreference.getData(Util.ROOM_UUID_KEY, getContext());
        initService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ChatViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Configurar el fondo
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT; // Para ocupar toda la pantalla
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                behavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.875)); // 87.5% de la pantalla

                behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            if (msgAdapter.getItemCount() > 0) {
                                binding.msgadpter.scrollToPosition(msgAdapter.getItemCount() - 1); // Desplazar al último mensaje
                            }
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        // Opcional: Lógica durante el deslizamiento
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.msgadpter.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.msgadpter.setLayoutManager(linearLayoutManager);
        binding.msgadpter.setAdapter(msgAdapter);

        binding.sendbtnn.setOnClickListener(v -> {
            String message = binding.textmsg.getText().toString();
            if (message.isEmpty()){
                Toast.makeText(requireContext(), "Escribe un mensaje primero", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.textmsg.setText("");
            Date date = new Date();
            long time = date.getTime();

            chatService.sendMessage(message,time, new ActionCallback() {
                @Override
                public void onSuccess() {
                    Util.showLog("BottomChatView", "Mensaje enviado");
                }

                @Override
                public void onFailure(Exception e) {
                    Util.showLog("BottomChatView", "Error al enviar mensaje");
                }
            });
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initListener();
        if (msgAdapter.getItemCount() > 0) {
            binding.msgadpter.scrollToPosition(msgAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListener();
    }

    //cuando se quita del stack
    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListener();
    }

    public void initService(){
        chatService = new ChatService(requireContext());
        msgAdapter = new MessageAdapter(requireContext(), new ArrayList<>());
    }

    public void initListener(){
        if (velChat == null) {
            Util.showLog("BottomChatView", "Iniciando listener");
            velChat = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        msgAdapter.clearMessages();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            UserMessage msg = data.getValue(UserMessage.class);
                            msgAdapter.addMessage(msg);
                        }
                        binding.msgadpter.scrollToPosition(msgAdapter.getItemCount() - 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog("BottomChatView", "Error al obtener mensajes");
                }
            };
            dbRef = FirebaseDatabase.getInstance().getReference("chat").child(idRoom).child("messages");
            dbRef.addValueEventListener(velChat);
        }
    }

    public void removeListener(){
        msgAdapter.clearMessages();
        if (velChat != null) {
            Util.showLog("BottomChatView", "Removiendo listener");
            dbRef.removeEventListener(velChat);
            velChat = null;
        }
    }
}
