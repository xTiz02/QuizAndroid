package com.prd.quizzoapp.views.acc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.prd.quizzoapp.databinding.ActivityRegisterBinding;
import com.prd.quizzoapp.model.service.AccountService;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.util.Util;

import org.jetbrains.annotations.Nullable;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AccountService accService;
    private final LoadingService ls = new LoadingService(this);
    private Uri imageURI;
    private String imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accService = new AccountService(this);

        binding.btnSignUp.setOnClickListener(v -> {
           if(!validateFields()) return;
           ls.showLoading("Creando usuario ...");
           accService.createUser(
                   imageURI,
                   binding.edtUsername.getText().toString().trim(),
                   binding.etdEmail.getText().toString().trim(),
                   binding.edtPassword.getText().toString().trim(),
                   new ActionCallback() {
                       @Override
                       public void onSuccess() {
                           ls.updateMessage("Usuario registrado con éxito");
                           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                           startActivity(intent);
                           finish();
                           ls.hideLoading();
                       }

                       @Override
                       public void onFailure(Exception e) {
                           ls.hideLoading();
                       }
                   });
        });

        binding.tvLoginPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.profilerg0.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Seleccionar Imagen"),10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageURI = data.getData();
                binding.profilerg0.setImageURI(imageURI);
            }
        }
    }



    private boolean validateFields() {
        String username = binding.edtUsername.getText().toString().trim();
        String email = binding.etdEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Por favor complete los campos", Toast.LENGTH_SHORT).show();
        }
        if(!email.matches(Util.EMAIL_PATTERN)){
            binding.etdEmail.requestFocus();
            binding.etdEmail.setError("Correo inválido");
            return false;
        }
        if(password.length() < 6) {
            binding.edtPassword.requestFocus();
            binding.edtPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return  false;
        }
        if(!password.equals(confirmPassword)){
            binding.edtConfirmPassword.requestFocus();
            binding.edtConfirmPassword.setError("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }
}