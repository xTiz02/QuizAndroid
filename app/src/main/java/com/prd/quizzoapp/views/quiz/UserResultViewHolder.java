package com.prd.quizzoapp.views.quiz;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.ItemUserResultBinding;
import com.prd.quizzoapp.model.entity.UserResult;
import com.squareup.picasso.Picasso;

public class UserResultViewHolder extends RecyclerView.ViewHolder {

    private ItemUserResultBinding binding;

    public UserResultViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemUserResultBinding.bind(itemView);
    }


    public void bind(UserResult userResult, int index, Context context) {
        binding.tvScore.setText(String.valueOf(userResult.getScore()));
        binding.tvUsername.setText(userResult.getUsername());
        //C:3  I:2  T:11.5
        binding.tvStats.setText("C:" + userResult.getCorrectAnswers() + " I:" + userResult.getWrongAnswers() + " T:" + userResult.getTime());
        binding.tvRank.setText(String.valueOf(index + 1));
        if (!userResult.isPlayed()){
            binding.tvRank.setTextColor(ContextCompat.getColor(context, R.color.red2));
        }
        Picasso.get().load(userResult.getImg()).into(binding.ivLeaderBoardProfilePic);
    }
}
