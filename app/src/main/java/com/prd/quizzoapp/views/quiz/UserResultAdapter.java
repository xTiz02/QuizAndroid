package com.prd.quizzoapp.views.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.model.entity.UserResult;

import java.util.ArrayList;

public class UserResultAdapter extends RecyclerView.Adapter<UserResultViewHolder> {

    private ArrayList<UserResult> users;
    private Context context;

    public UserResultAdapter(ArrayList<UserResult> users, Context context) {
        this.users = users;
        this.context = context;
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_result, parent, false);
        return new UserResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserResultViewHolder holder, int position) {
        UserResult user = users.get(position);
        holder.bind(user,position,context);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<UserResult> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void removeUser(String userUuid ) {
        UserResult user = users.stream().filter(u -> u.getUserUuid().equals(userUuid)).findFirst().orElse(null);
        if(user != null) {
            users.remove(user);
        }
        notifyDataSetChanged();
    }

    public ArrayList<UserResult> getUsers() {
        return users;
    }

    public void updateUser(UserResult user) {
        UserResult userToUpdate = users.stream().filter(u -> u.getUserUuid().equals(user.getUserUuid())).findFirst().orElse(null);
        if(userToUpdate != null) {
            userToUpdate.setScore(user.getScore());
            userToUpdate.setCorrectAnswers(user.getCorrectAnswers());
            userToUpdate.setWrongAnswers(user.getWrongAnswers());
            userToUpdate.setTime(user.getTime());
        }
        notifyDataSetChanged();
    }
}
