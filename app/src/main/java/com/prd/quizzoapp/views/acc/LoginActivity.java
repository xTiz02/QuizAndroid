package com.prd.quizzoapp.views.acc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.prd.quizzoapp.MainActivity;
import com.prd.quizzoapp.databinding.ActivityLoginBinding;
import com.prd.quizzoapp.model.service.AccountService;
import com.prd.quizzoapp.model.service.ActionCallback;
import com.prd.quizzoapp.model.service.LoadingService;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AccountService accountService;
    private final LoadingService ls = new LoadingService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        hideSystemUI();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accountService = new AccountService(this);

        //Cundo se de click en el botón de login redirigir a la pantalla principal donde se muestra el BottomNavigationView y los fragments
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.etSinInPassword.getText().toString().trim();
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
            accountService.login(
                    binding.edtEmail.getText().toString().trim(),
                    binding.etSinInPassword.getText().toString().trim(),
                    new ActionCallback() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void hideSystemUI() {
        // Ocultar la barra de navegación y poner la pantalla en modo inmersivo
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}