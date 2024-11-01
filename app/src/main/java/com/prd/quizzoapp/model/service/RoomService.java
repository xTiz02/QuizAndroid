package com.prd.quizzoapp.model.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prd.quizzoapp.model.entity.Category;
import com.prd.quizzoapp.model.entity.Room;
import com.prd.quizzoapp.model.entity.RoomConfig;
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
        String userUUID = auth.getUid().toString();
        Room room = new Room(new RoomConfig(roomUUID,userUUID,time,questions,roomCode),subCategories,categories);
        dbRef.child(roomUUID).child("settings").setValue(room).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                us.saveUserRoom(roomUUID, userUUID, true, new ActionCallback() {
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
        dbRef.child(roomUuid).child("settings/roomConfig").updateChildren(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                Util.showLog(TAG, "Error al actualizar sala");
                callback.onFailure(task.getException());
            }
        });
    }


    public void findAndJoinRoom(String roomCode, DataActionCallback<RoomConfig> callback){
        System.out.println("Code to find: "+roomCode);
        Query query = dbRef.orderByChild("settings/roomConfig/code").equalTo(roomCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        RoomConfig roomConfig = ds.child("settings/roomConfig").getValue(RoomConfig.class);
                        DataSharedPreference.saveData(Util.ROOM_UUID_KEY, roomConfig.getUuid(), context);
                        us.saveUserRoom(roomConfig.getUuid(), auth.getUid(), false, new ActionCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onSuccess(roomConfig);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                callback.onFailure(e);
                            }
                        });
                        break;
                    }
                }else {
                    callback.onFailure(new Exception("No se encontró la sala"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(new Exception("Error al buscar sala"));
            }
        });
    }


    public void deleteRoom(String roomUUID,String userUuid, boolean isAdmin, ActionCallback callback){
        if(isAdmin){
            dbRef.child(roomUUID).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    callback.onSuccess();
                }else {
                    callback.onFailure(task.getException());
                }
            });
        }else {
            dbRef.child(roomUUID).child("usersRoom").child(userUuid).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DataSharedPreference.removeData(Util.ROOM_UUID_KEY, context);
                    callback.onSuccess();
                }else {
                    callback.onFailure(task.getException());
                }
            });
        }
    }
}
