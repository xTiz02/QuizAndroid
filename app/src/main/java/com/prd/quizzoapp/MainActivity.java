package com.prd.quizzoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prd.quizzoapp.databinding.ActivityMainBinding;
import com.prd.quizzoapp.model.dto.QuestionsListDto;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;
import com.prd.quizzoapp.views.quiz.QuizActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SseManager.SseListener {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DatabaseReference dbRef;
    private NavController navController;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //hideSystemUI();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initNavigation();
        SseManager.getInstance().addListener(this);
         auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    System.out.println("snapshot: "+snapshot);
                    binding.tvUserName.setText(snapshot.child("username").getValue().toString());
                    Picasso.get().load(snapshot.child("img").getValue().toString()).into(binding.mainProfileImage);
                }else {
                    binding.tvUserName.setText("loading...");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showLog(TAG, "Error al obtener la sala del usuario");
            }
        });
    }
    @Override
    public void onMessageReceived(QuestionsListDto questions) {
        //esperar 4 segundos para que el usuario pueda leer el mensaje
        Util.delay(4000,"La partida va a comenzar ...",this,
                ()->{//before
                },
                () -> {
                    Intent intent = new Intent(this, QuizActivity.class);
                    intent.putExtra("questions", new ArrayList<>(questions.getQuestions()));
                    startActivity(intent);
                    finish();
                });
    }
    @Override
    public void onConnectionError(Throwable t) {
        //DataSharedPreference.removeData(Util.ROOM_UUID_KEY, this);
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        navController.navigate(R.id.homeFragment);
        Util.showLog(TAG,"Error en la conexión escuchando en main:"+ t.getMessage());
        Toast.makeText(this, "Error en la conexión de la sala", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClosed() {
        DataSharedPreference.removeData(Util.ROOM_UUID_KEY, this);
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        navController.navigate(R.id.notRoomFragment);
        Toast.makeText(this, "Se cerro la sala", Toast.LENGTH_LONG).show();
        Util.showLog("SseManager", "Conexión cerrada por el servidor");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SseManager.getInstance().removeListener(this);
        SseManager.getInstance().disconnect();
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
            }else {
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