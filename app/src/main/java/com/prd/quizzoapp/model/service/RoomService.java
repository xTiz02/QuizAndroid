package com.prd.quizzoapp.model.service;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prd.quizzoapp.model.entity.Category;
import com.prd.quizzoapp.model.entity.Room;
import com.prd.quizzoapp.model.entity.RoomConfig;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import java.util.ArrayList;
import java.util.Map;

public class RoomService {
    private final String TAG = "RoomService";
    private FirebaseAuth auth;
    private Context context;
    private LoadingService ls;
    private UserService us;
    private DatabaseReference dbRef;

    public RoomService(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        ls = new LoadingService(context);
        us = new UserService(context);
        dbRef = FirebaseDatabase.getInstance().getReference("rooms");
    }

    public void createRoom(String roomCode,  int questions, int time,
                           ArrayList<Category> categories, ArrayList<String> subCategories, ActionCallback callback) {
        String roomUUID = dbRef.push().getKey();
        us.getUser(auth.getCurrentUser().getUid(), new DataActionCallback<User>() {
            @Override
            public void onSuccess(User data) {
                Room room = new Room(new RoomConfig(roomUUID,auth.getCurrentUser().getUid(),time,questions,roomCode),
                        subCategories,categories);
                dbRef.child(roomUUID).child("settings").setValue(room).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        UserRoom userRoom = new UserRoom(data.getUUID(), data.getUsername(), data.getDescription(), data.getImg(),false,true);
                        us.saveUserRoom(roomUUID, userRoom, new ActionCallback() {
                            @Override
                            public void onSuccess() {
                                DataSharedPreference.saveData(Util.ROOM_UUID_KEY, roomUUID, context);
                                callback.onSuccess();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                Util.showLog(TAG, "Error al guardar usuario en sala");
                                callback.onFailure(e);
                            }
                        });
                    }else {
                        Util.showLog(TAG, "Error al guardar sala");
                        callback.onFailure(task.getException());
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Util.showLog(TAG,"Error al obtener usuario que crea la sala");
                callback.onFailure(e);
            }
        });

    }

    public void getRoomByUuid(String roomUUID, DataActionCallback<Room> callback){
        dbRef.child(roomUUID).child("settings").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Room room = task.getResult().getValue(Room.class);
                Log.d(TAG, "Room: "+room);
                callback.onSuccess(room);
            }else {
                Util.showLog(TAG, "Error al obtener sala");
                callback.onFailure(task.getException());
            }
        });
    }

    public void updateRoom(String roomUuid,Map<String,Object> data, ActionCallback callback){
        dbRef.child(roomUuid).child("settings").updateChildren(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                Util.showLog(TAG, "Error al actualizar sala");
                callback.onFailure(task.getException());
            }
        });
    }
}
