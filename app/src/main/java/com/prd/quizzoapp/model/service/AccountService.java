package com.prd.quizzoapp.model.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.prd.quizzoapp.MainActivity;
import com.prd.quizzoapp.databinding.ActivityLoginBinding;
import com.prd.quizzoapp.databinding.ActivityRegisterBinding;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.util.Util;
import com.prd.quizzoapp.views.acc.LoginActivity;
import com.prd.quizzoapp.views.acc.RegisterActivity;

public class AccountService {

    private final String TAG = "AccountService";
    private FirebaseAuth auth;
    private Context context;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private LoadingService ls;
    private UserService us;

    public AccountService(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        ls = new LoadingService(context);
        us = new UserService(context);
    }

    public void createUser(String username, String email, String password, String confirmPassword, ActivityRegisterBinding binding) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(context, "Por favor complete los campos", Toast.LENGTH_SHORT).show();
        }
        if(!email.matches(emailPattern)){
            binding.etdEmail.requestFocus();
            binding.etdEmail.setError("Correo inválido");
            return;
        }
        if(password.length() < 6) {
            binding.edtPassword.requestFocus();
            binding.edtPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if(!password.equals(confirmPassword)){
            binding.edtConfirmPassword.requestFocus();
            binding.edtConfirmPassword.setError("Las contraseñas no coinciden");
            return ;
        }
        ls.showLoading("Creando usuario ...");
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User user = new User(
                        task.getResult().getUser().getUid(),
                        username,email,password, "Hola, estoy usando QuizzoApp",
                        Util.default_img);
                us.saveUser(user, new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        ls.updateMessage("Usuario registrado con éxito");
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((RegisterActivity) context).finish();
                        ls.hideLoading();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Util.showLog(TAG, "Error al crear usuario",context,e);
                        ls.hideLoading();
                    }
                });
            }else{
                Util.showLog(TAG, "Error al crear usuario",context,task.getException());
                ls.hideLoading();

            }
        });
    }



    public void login(String email, String password, ActivityLoginBinding binding){
        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Ingresar correo");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.etSinInPassword.requestFocus();
            binding.etSinInPassword.setError("Ingresar contraseña");
            return;
        }
        ls.showLoading("Iniciando sesión ...");
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ls.updateMessage("Inicio de sesión exitoso");
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((LoginActivity) context).finish();
                ls.hideLoading();
            } else {
                Util.showLog(TAG, "Error al iniciar sesión", context, task.getException());
                ls.hideLoading();
            }
        });
    }
}
