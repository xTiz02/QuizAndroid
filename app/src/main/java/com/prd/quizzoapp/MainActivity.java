package com.prd.quizzoapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prd.quizzoapp.databinding.ActivityMainBinding;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //hideSystemUI();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initNavigation();



    }


    private void initNavigation(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        // Configura el BottomNavigationView con NavController
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Si no hay una sala creada, se muestra el fragmento NotRoomFragment en lugar de RoomFragment
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.roomFragment) {
                if(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this) == null) {
                    navController.navigate(R.id.notRoomFragment);
                }else {
                    navController.navigate(item.getItemId());
                }
            }else{
                navController.navigate(item.getItemId());
            }
            return true;
        });

        // Oculta el BottomNavigationView cuando se navega a CreateRoomFragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.createRoomFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideSystemUI() {
        // Ocultar la barra de navegación y poner la pantalla en modo inmersivo
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION


        );
    }
}