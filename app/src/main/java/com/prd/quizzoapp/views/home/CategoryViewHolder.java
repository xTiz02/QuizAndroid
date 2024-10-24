package com.prd.quizzoapp.views.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.ItemCategoryBinding;
import com.prd.quizzoapp.model.entity.Category;
import com.squareup.picasso.Picasso;

public class CategoryViewHolder extends RecyclerView.ViewHolder{

    private ItemCategoryBinding binding;


    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemCategoryBinding.bind(itemView);
    }


    public void bind(Category category, Context context) {
        binding.category.setText(category.getCategory().getName());
        Picasso.get().load(category.getImg()).into(binding.image);
        binding.cardView.setOnClickListener(v -> {
            if (category.isSelected()) {
                binding.cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray_3)));
                binding.category.setTextColor(ContextCompat.getColor(context,R.color.light_gray_1));
                category.setSelected(false);
            }else {
                binding.cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_back)));
                binding.category.setTextColor(ContextCompat.getColor(context,R.color.dark_gray_3));
                category.setSelected(true);
            }
        });
    }
}
