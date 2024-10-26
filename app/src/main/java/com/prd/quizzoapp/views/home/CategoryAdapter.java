package com.prd.quizzoapp.views.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.model.entity.Category;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder>{
    private Context context;
    private ArrayList<Category> categories;
    private OnClickCategory onClickCategory;

    public CategoryAdapter(ArrayList<Category> categories, Context context, OnClickCategory onClickCategory) {
        this.categories = categories;
        this.context = context;
        this.onClickCategory = onClickCategory;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category,context,onClickCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<Category> getSelectedCategories() {
        return  categories.stream().filter(Category::isSelected).collect(Collectors.toCollection(ArrayList::new));
    }

    public interface OnClickCategory {
        void onClickCategory();
    }
}


