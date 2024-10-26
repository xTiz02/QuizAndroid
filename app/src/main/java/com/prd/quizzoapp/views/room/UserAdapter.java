package com.prd.quizzoapp.views.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.model.entity.UserRoom;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private ArrayList<UserRoom> userRooms;

    public UserAdapter(ArrayList<UserRoom> userRooms) {
        this.userRooms = userRooms;
    }

    public ArrayList<UserRoom> getUsers() {
        return userRooms;
    }

    public void setUsers(ArrayList<UserRoom> userRooms) {
        this.userRooms = userRooms;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_room, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserRoom userRoom = userRooms.get(position);
        holder.bind(userRoom);
    }

    @Override
    public int getItemCount() {
        return userRooms.size();
    }

    public void addUser(UserRoom userRoom) {
        userRooms.add(userRoom);
        notifyItemInserted(userRooms.size() - 1);
    }

    public void removeUser(String uuid) {
        UserRoom userRoom = userRooms.stream().filter(u -> u.getUUID().equals(uuid)).findFirst().orElse(null);
        int index = userRooms.indexOf(userRoom);
        userRooms.remove(userRoom);
        notifyItemRemoved(index);
    }
}
