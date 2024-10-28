package com.prd.quizzoapp.model.service;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.util.Util;

public class AccountService {

    private final String TAG = "AccountService";
    private FirebaseAuth auth;
    private Context context;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private UserService us;

    public AccountService(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        us = new UserService(context);
    }

    public void createUser(String username, String email, String password,ActionCallback callback){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User user = new User(
                        task.getResult().getUser().getUid(),
                        username,email,password, "Hola, estoy usando QuizzoApp",
                        Util.default_img);
                us.saveUser(user, new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Util.showLog(TAG,"Error al guardar usuario");
                        callback.onFailure(e);
                    }
                });
            }else{
                Util.showLog(TAG, "Error al crear usuario");
                callback.onFailure(task.getException());
            }
        });
    }



    public void login(String email, String password, ActionCallback callback) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                Util.showLog(TAG, "Error al iniciar sesión");
                callback.onFailure(task.getException());
            }
        });
    }
}
