package com.prd.quizzoapp.views.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prd.quizzoapp.MainActivity;
import com.prd.quizzoapp.databinding.ActivityLeaderBoardBinding;
import com.prd.quizzoapp.model.entity.UserResult;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = "LeaderBoardActivity";
    private ActivityLeaderBoardBinding binding;
    private UserResultAdapter adapter;
    private DatabaseReference dbRef;
    private RoomService rS;
    private FirebaseAuth auth;
    private ValueEventListener eventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initServices();
        binding.leaderBoardRecyclerView.setHasFixedSize(true);
        binding.leaderBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.leaderBoardRecyclerView.setAdapter(adapter);

        binding.btnBackToRoom.setOnClickListener(v -> {
            SseManager.getInstance().connect(DataSharedPreference.getData(
                    Util.ROOM_UUID_KEY, this
            ));
            rS.changePlayingState(
                    DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),
                    auth.getUid().toString(),
                    false
            );
            Util.showLog(TAG,"Redirigiendo al main laderboard");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void printTop3(ArrayList<UserResult> results) {
        Picasso.get().load(results.get(0).getImg()).into(binding.ivRank1);
        binding.tvRank1Name.setText(results.get(0).getUsername());
        binding.tvRank1Score.setText(String.valueOf(results.get(0).getScore()));

        if(results.size()>1){
            Picasso.get().load(results.get(1).getImg()).into(binding.ivRank2);
            binding.tvRank2Name.setText(results.get(1).getUsername());
            binding.tvRank2Score.setText(String.valueOf(results.get(1).getScore()));
        }
        if(results.size()>2){
            Picasso.get().load(results.get(2).getImg()).into(binding.ivRank3);
            binding.tvRank3Name.setText(results.get(2).getUsername());
            binding.tvRank3Score.setText(String.valueOf(results.get(2).getScore()));
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListener(); // Inicia el listener cuando la actividad está activa.
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener(); // Detiene el listener cuando la actividad está en pausa.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
    }

    public  void removeListener(){
        adapter.clear();
        if(eventListener != null){
            Util.showLog("LeaderBoardActivity", "Se removio el listener");
            dbRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }
    public void initListener(){
        if(eventListener == null){
            Util.showLog("LeaderBoardActivity", "Se agrego el listener");
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Util.showLog("LeaderBoardActivity", "Se obtuvieron los resultados");
                        ArrayList<UserResult> userList = new ArrayList<>();
                        for(DataSnapshot userSnapshot : snapshot.getChildren()){
                            UserResult user = userSnapshot.getValue(UserResult.class); // Asegúrate de que User tenga el campo "score"
                            if(user != null){
                                userList.add(user);
                            }
                        }
                        userList.sort((u1, u2) -> Double.compare(u2.getScore(), u1.getScore()));
                        printTop3(userList);
                        adapter.setUsers(userList);

                    }else {
                        Toast.makeText(LeaderBoardActivity.this, "No hay resultados", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog("LeaderBoardActivity", "Error al obtener los resultados");
                    Toast.makeText(LeaderBoardActivity.this, "Error al obtener los resultados", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LeaderBoardActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            };
            dbRef.addValueEventListener(eventListener);
        }
    }

    public void initServices() {
        auth = FirebaseAuth.getInstance();
        adapter = new UserResultAdapter(new ArrayList<>(), this);
        rS = new RoomService(this);
        dbRef = FirebaseDatabase.getInstance().getReference("results")
                .child(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this))
                .child("users");
    }

    //cuando se presiona el boton de regresar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}