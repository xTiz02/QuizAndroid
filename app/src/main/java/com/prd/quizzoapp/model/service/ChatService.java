package com.prd.quizzoapp.model.service;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prd.quizzoapp.model.entity.UserMessage;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

public class ChatService {
    private final String TAG = "ChatService";
    private Context context;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    public ChatService(Context context) {
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void sendMessage(String message,long time, ActionCallback callback){
        DatabaseReference reference = database.getReference().child("chat")
                .child(DataSharedPreference.getData(Util.ROOM_UUID_KEY, context))
                .child("messages");
        UserMessage userMessage = new UserMessage(message,auth.getUid().toString(),time);
        reference.push().setValue(userMessage).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Util.showLog(TAG, "Mensaje guardado");
                callback.onSuccess();
            }else {
                Util.showLog(TAG, "Error al guardar mensaje");
                callback.onFailure(task.getException());
            }
        });
    }

    public void deleteChat(ActionCallback callback){
        DatabaseReference reference = database.getReference().child("chat")
                .child(DataSharedPreference.getData(Util.ROOM_UUID_KEY, context));
        reference.removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Util.showLog(TAG, "Chat eliminado");
                callback.onSuccess();
            }else {
                Util.showLog(TAG, "Error al eliminar chat");
                callback.onFailure(task.getException());
            }
        });
    }
}
