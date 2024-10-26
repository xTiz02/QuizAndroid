package com.prd.quizzoapp.model.service;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prd.quizzoapp.model.entity.User;

public class UserService {
    private Context context;
    private FirebaseDatabase database;

    public UserService(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
    }

    public void saveUser(User user, ActionCallback callback){
        DatabaseReference reference = database.getReference().child("users").child(user.getUUID());
        reference.setValue(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            }else {
                callback.onFailure(task.getException());
            }
        });
    }
}
