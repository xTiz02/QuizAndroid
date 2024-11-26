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
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.ResultService;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.model.service.UserService;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
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
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String idUser = auth.getUid();
    private RoomService roomService;
    private ResultService resultService;
    private UserService userService;
    private LoadingService loadingService;
    private ValueEventListener valueUserListener;
    private boolean logout = false;
    private boolean gameStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.showLog(TAG, "onCreate");
        EdgeToEdge.enable(this);
        //hideSystemUI();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initServices();
        /*binding.ivLogout.setOnClickListener(v -> {
             loadingService.signOutDialog(()->{
                 logout = true;
                 dbRef.removeEventListener(valueUserListener);
                Util.showLog(TAG, "Saliendo de la app");
                 if(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this) != null) {
                     boolean isAdmin = DataSharedPreference.getBooleanData(Util.IS_ADMIN_KEY, this);
                     roomService.deleteRoom(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),
                             idUser,
                             isAdmin,
                             new ActionCallback() {
                                 @Override
                                 public void onSuccess() {

                                     Util.showLog(TAG, "Sala eliminada");
                                     logout = true;

                                     DataSharedPreference.removeData(Util.ROOM_UUID_KEY, MainActivity.this);
                                     DataSharedPreference.removeData(Util.IS_ADMIN_KEY, MainActivity.this);

                                 }

                                 @Override
                                 public void onFailure(Exception e) {
                                     Toast.makeText(MainActivity.this, "Error al salir de la app", Toast.LENGTH_SHORT).show();
                                 }
                             });
                 }else {

                     Util.showLog(TAG, "Saliendo de la app sin la sala");
                     logout = true;

                    //cerrar todas las actividades y fragmentos
                    //eliminar la navegación de la pila de retroceso
                 }
                 FirebaseAuth.getInstance().signOut();
                 Util.showLog(TAG, "Redirigiendo a la pantalla de inicio de sesión");
                 //cerrar la app
                 NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                         .findFragmentById(R.id.fragmentContainerView);
                 if (navHostFragment != null) {
                     NavController navController = navHostFragment.getNavController();


                     navController.popBackStack(R.id.main_graph, true); // Reemplaza 'nav_graph' con el ID de tu gráfico raíz
                 }
             });
         });*/





    }
    @Override
    public void onMessageReceived(QuestionsListDto questions) {
        //esperar 4 segundos para que el usuario pueda leer el mensaje
        /*Util.delay("La partida va a comenzar ...",this,
                ()->{
                    gameStart = true;
                    Util.showLog(TAG, "La partida va a comenzar ...");
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra("questions", new ArrayList<>(questions.getQuestions()));
                    startActivity(intent);
                    finishAffinity();
                });*/
        Util.delay(4000, "La partida va a comenzar ...", this,
                () -> {
                    gameStart = true;
                    //roomService.changePlayingState(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),idUser, true);
                },
                () -> {
                    Util.showLog(TAG, "La partida va a comenzar ...");
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra("questions", new ArrayList<>(questions.getQuestions()));
                    startActivity(intent);
                    finishAffinity();
                });
    }
    @Override
    public void onConnectionError(Throwable t) {
        //DataSharedPreference.removeData(Util.ROOM_UUID_KEY, this);
        if (!logout && !gameStart) {
            Util.delay(2000, "Intentando volver a la sala", this,
                    () -> {//before
                    },
                    () -> {
                        if(DataSharedPreference.containsData(Util.ROOM_UUID_KEY, this)){
                            roomService.changePlayingState(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),idUser, true);
                        }
                        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
                        navController.navigate(R.id.homeFragment);
                        Util.showLog(TAG, "Error en la conexión escuchando en main");

                    });
        }
    }
    @Override
    public void onClosed() {
        if(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this) != null && !logout) {
            Util.delay(4000,"La sala ya no está disponible",this,
                    ()->{
                    roomService.deleteRoom(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),
                            idUser,
                            DataSharedPreference.getBooleanData(Util.IS_ADMIN_KEY, this),
                            new ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    DataSharedPreference.clearData(MainActivity.this);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(MainActivity.this, "Error al cerrar la sala", Toast.LENGTH_SHORT).show();
                                }
                            });
                    },
                    () -> {
                        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
                        navController.navigate(R.id.homeFragment);
                        Util.showLog(TAG,"El servidor cerro la sala main");
                    });
        }
    }





    @Override
    protected void onStart() {
        super.onStart();
        Util.showLog(TAG, "onStart");
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.showLog(TAG, "onResume");
        initListener(); // Inicia el listener cuando la actividad está activa.
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.showLog(TAG, "onPause");
        removeListener(); // Detiene el listener cuando la actividad está en pausa.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
        if(DataSharedPreference.containsData(Util.ROOM_UUID_KEY, this)) {
            SseManager.getInstance().removeListener(this);
            SseManager.getInstance().disconnect();
        }
    }

    public void initListener(){
        if(valueUserListener==null){
            valueUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String img = snapshot.child("img").getValue().toString();
                        Util.showLog(TAG, "Usuario obtenido 2");
                        binding.tvUserName.setText(snapshot.child("username").getValue().toString());
                        Picasso.get().load(img).into(binding.mainProfileImage);
                    }else {
                        binding.tvUserName.setText("loading...");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog(TAG, "Error al obtener los datos del usuario");
                }
            };
            dbRef.addValueEventListener(valueUserListener);
        }
    }

    public void removeListener(){
        if(valueUserListener!=null){
            dbRef.removeEventListener(valueUserListener);
            valueUserListener = null;
        }
    }

    public void initServices(){
        initNavigation();
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(idUser);
        loadingService = new LoadingService(this);
        roomService = new RoomService(this);
        resultService = new ResultService(this);
        userService = new UserService(this);
        SseManager.getInstance().addListener(this);
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