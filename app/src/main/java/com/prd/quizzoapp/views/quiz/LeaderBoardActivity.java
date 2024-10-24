package com.prd.quizzoapp.views.quiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.prd.quizzoapp.databinding.ActivityLeaderBoardBinding;
import com.prd.quizzoapp.model.entity.UserResult;
import com.prd.quizzoapp.util.Data;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private ActivityLeaderBoardBinding binding;
    //private UserResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<UserResult> userResults = Data.getUserResults();
    }
}