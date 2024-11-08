package com.prd.quizzoapp.model.service;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.util.Util;

public class AccountService {

    private final String TAG = "AccountService";
    private FirebaseAuth auth;
    private Context context;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private UserService us;
    private FirebaseStorage storage;

    public AccountService(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        us = new UserService(context);
        storage = FirebaseStorage.getInstance();
    }

    public void createUser(Uri imageUri,String username, String email, String password, ActionCallback callback){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String uid = task.getResult().getUser().getUid();
                StorageReference storageReference = storage.getReference().child("upload").child(uid);
                if(imageUri!=null){
                    storageReference.putFile(imageUri).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                User user = new User(uid,username,email,password,"Hola...>_<",uri.toString());
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
                            });
                        }else{
                            Util.showLog(TAG, "Error al subir imagen");
                        }
                    });
                }else{
                    User user = new User(uid,username,email,password,"Hola...>_<",Util.default_img);
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
                }
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
