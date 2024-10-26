package com.prd.quizzoapp.views.acc;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.prd.quizzoapp.databinding.ActivityLoginBinding;
import com.prd.quizzoapp.model.service.AccountService;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AccountService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accountService = new AccountService(this);

        //Cundo se de click en el botón de login redirigir a la pantalla principal donde se muestra el BottomNavigationView y los fragments
        binding.btnLogin.setOnClickListener(v -> {
            accountService.login(
                    binding.edtEmail.getText().toString().trim(),
                    binding.etSinInPassword.getText().toString().trim(),
                    binding);
        });

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}