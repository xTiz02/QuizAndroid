package com.prd.quizzoapp.views.quiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prd.quizzoapp.databinding.ActivityLeaderBoardBinding;
import com.prd.quizzoapp.model.entity.UserResult;
import com.prd.quizzoapp.util.Data;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LeaderBoardActivity extends AppCompatActivity {

    private ActivityLeaderBoardBinding binding;
    private UserResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<UserResult> userResults = Data.getUserResults();
        System.out.println(userResults.size());
        adapter = new UserResultAdapter(userResults);
        binding.leaderBoardRecyclerView.setHasFixedSize(true);
        binding.leaderBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.leaderBoardRecyclerView.setAdapter(adapter);
        printTop3(userResults);
    }

    private void printTop3(ArrayList<UserResult> results) {
        ArrayList<UserResult> top3 = results.stream()
                .sorted((o1, o2) -> (int) (o2.getScore() - o1.getScore()))
                .limit(3)
                .collect(Collectors.toCollection(ArrayList::new));
        binding.ivRank1.setImageResource(top3.get(0).getImg());
        binding.tvRank1Name.setText(top3.get(0).getUsername());
        binding.tvRank1Score.setText(String.valueOf(top3.get(0).getScore()));

        binding.ivRank2.setImageResource(top3.get(1).getImg());
        binding.tvRank2Name.setText(top3.get(1).getUsername());
        binding.tvRank2Score.setText(String.valueOf(top3.get(1).getScore()));

        binding.ivRank3.setImageResource(top3.get(2).getImg());
        binding.tvRank3Name.setText(top3.get(2).getUsername());
        binding.tvRank3Score.setText(String.valueOf(top3.get(2).getScore()));
    }
}