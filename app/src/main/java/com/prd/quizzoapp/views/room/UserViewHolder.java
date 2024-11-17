package com.prd.quizzoapp.views.room;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.ItemUserRoomBinding;
import com.prd.quizzoapp.model.entity.UserRoom;
import com.squareup.picasso.Picasso;

public class UserViewHolder extends RecyclerView.ViewHolder{

    private ItemUserRoomBinding binding;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemUserRoomBinding.bind(itemView);
    }



    public void bind(UserRoom userRoom, Context context) {
        binding.tvUsername.setText(userRoom.getUsername());
        Picasso.get().load(userRoom.getImg()).into(binding.ivPictureProfile);
        binding.tvDescription.setText(userRoom.getDescription());
        binding.statusCircle.setBackgroundResource(userRoom.isAdmin() ? R.drawable.green_cricle : R.drawable.default_circle);
        if(userRoom.isPlaying()){
            binding.cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray_2)));
            binding.verticalLine.setBackgroundResource(R.color.dark_gray_2);
            binding.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.light_grey_2));
        }else{
            binding.cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray_1)));
            binding.verticalLine.setBackgroundResource(R.color.dark_gray_1);
            binding.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
        }

    }
}
