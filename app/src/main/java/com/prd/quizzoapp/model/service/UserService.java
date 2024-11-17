package com.prd.quizzoapp.model.service;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
import com.prd.quizzoapp.util.Util;

public class UserService {
    private final String TAG = "UserService";
    private Context context;
    private FirebaseDatabase database;

    public UserService(Context context) {
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
    }

    public void saveUser(User user, ActionCallback callback){
        DatabaseReference reference = database.getReference().child("users").child(user.getUUID());
        reference.setValue(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                Util.showLog(TAG, "Error al guardar usuario");
                callback.onFailure(task.getException());
            }
        });
    }

    public void getUser(String uuid, DataActionCallback<User> callback){
        DatabaseReference reference = database.getReference().child("users").child(uuid);
        reference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DataSnapshot snapshot = task.getResult();
                if(snapshot.exists()){//se verifica si el usuario tiene datos y no es nulo
                    callback.onSuccess(snapshot.getValue(User.class));
                }
            }else {
                Util.showLog(TAG, "Error al obtener usuario");
                callback.onFailure(task.getException());
            }
        });
    }

    public void saveUserRoom(String roomUuid, String userUuid,boolean isAdmin, ActionCallback callback){
        DatabaseReference reference = database.getReference("rooms").child(roomUuid).child("usersRoom");
        getUser(userUuid, new DataActionCallback<User>() {
            @Override
            public void onSuccess(User data) {
                UserRoom userRoom = new UserRoom(data.getUUID(), data.getUsername(), data.getDescription(), data.getImg(), false, isAdmin);
                reference.child(userRoom.getUUID()).setValue(userRoom).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        callback.onSuccess();
                    }else {
                        Util.showLog(TAG, "Error al guardar usuario en sala");
                        callback.onFailure(task.getException());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Util.showLog(TAG, "Error al obtener usuario");
                callback.onFailure(e);
            }
        });
    }




}
