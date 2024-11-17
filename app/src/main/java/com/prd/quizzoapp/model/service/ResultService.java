package com.prd.quizzoapp.model.service;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prd.quizzoapp.model.entity.Score;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.entity.UserResult;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
import com.prd.quizzoapp.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultService {
    private final String TAG = "ResultService";
    private FirebaseAuth auth;
    private Context context;
    private UserService us;
    private DatabaseReference dbRef;

    public ResultService(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        us = new UserService(context);
        dbRef = FirebaseDatabase.getInstance().getReference("results");
    }

    public void saveUserResult(String roomUUID, ActionCallback callback) {
        String userId = auth.getUid().toString();
        us.getUser(userId, new DataActionCallback<User>() {
            @Override
            public void onSuccess(User data) {
                UserResult userResult = new UserResult(data.getUUID(), 0, data.getUsername(), data.getImg(), 0, 0, 0, true);
                dbRef.child(roomUUID).child("users").child(userId).setValue(userResult).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    }else {
                        Util.showLog(TAG, "Error al guardar user Result en: "+roomUUID);
                        callback.onFailure(new Exception("Error al guardar user Result en: "+roomUUID));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Util.showLog(TAG, "Error al obtener userResult en: "+userId);
                callback.onFailure(e);
            }
        });
    }

    public void updateScore(String roomUUID, ArrayList<Score> scores, ActionCallback callback){
        String userId = auth.getUid().toString();
        Map<String, Object> scoreMap = new HashMap<>();
        //sumar los scores y añadirlos al map
        scores.stream().forEach(score -> {
            if(scoreMap.containsKey("score")){
                scoreMap.put("score", (double)scoreMap.get("score")+score.getScore());
            }else {
                scoreMap.put("score", score.getScore());
            }
            if(scoreMap.containsKey("time")){
                scoreMap.put("time", (double)scoreMap.get("time")+score.getTime());
            }else {
                scoreMap.put("time", score.getTime());
            }
            if(score.isMarkedCorrect()){
                if(scoreMap.containsKey("correctAnswers")){
                    scoreMap.put("correctAnswers", (int)scoreMap.get("correctAnswers")+1);
                }else {
                    scoreMap.put("correctAnswers", 1);
                }
            }else {
                if(scoreMap.containsKey("wrongAnswers")){
                    scoreMap.put("wrongAnswers", (int)scoreMap.get("wrongAnswers")+1);
                }else {
                    scoreMap.put("wrongAnswers", 1);
                }
            }
        });
        dbRef.child(roomUUID).child("users").child(userId).updateChildren(scoreMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                callback.onFailure(new Exception("Error al guardar score en: "+roomUUID));
            }
        });
    }
    public void deleteUserResult(String roomUUID,String userId, ActionCallback callback){
        dbRef.child(roomUUID).child("users").child(userId).removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                callback.onFailure(new Exception("Error al eliminar userResult en: "+roomUUID));
            }
        });
    }

    public void deleteAllUserResults(String roomUUID, ActionCallback callback){
        dbRef.child(roomUUID).child("users").removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                callback.onFailure(new Exception("Error al eliminar userResults en: "+roomUUID));
            }
        });
    }
}
