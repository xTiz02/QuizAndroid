package com.prd.quizzoapp.views.room;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.ItemUserRoomBinding;
import com.prd.quizzoapp.model.entity.User;
import com.squareup.picasso.Picasso;

public class UserViewHolder extends RecyclerView.ViewHolder{

    private ItemUserRoomBinding binding;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemUserRoomBinding.bind(itemView);
    }

    public void bind(User user) {
        binding.tvUsername.setText(user.getUsername());
        Picasso.get().load(user.getImg()).into(binding.ivPictureProfile);
        binding.tvDescription.setText(user.getDescription());
        binding.statusCircle.setBackgroundResource(user.isAdmin() ? R.drawable.green_cricle : R.drawable.default_circle);
    }
}
