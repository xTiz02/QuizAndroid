package com.prd.quizzoapp.views.acc;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.prd.quizzoapp.databinding.ActivityRegisterBinding;
import com.prd.quizzoapp.model.service.AccountService;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AccountService accService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accService = new AccountService(this);

        binding.btnSignUp.setOnClickListener(v -> {
            accService.createUser(
                binding.edtUsername.getText().toString().trim(),
                binding.etdEmail.getText().toString().trim(),
                binding.edtPassword.getText().toString().trim(),
                binding.edtConfirmPassword.getText().toString().trim(),
                binding);
        });

        binding.tvLoginPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}