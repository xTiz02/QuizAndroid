package com.prd.quizzoapp.views.room;

import android.content.Context;
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
    private Context context;

    public UserAdapter(ArrayList<UserRoom> userRooms, Context context) {
        this.userRooms = userRooms;
        this.context = context;
    }

    public ArrayList<UserRoom> getUsers() {
        return userRooms;
    }

    public void setUsers(ArrayList<UserRoom> userRooms) {
        this.userRooms = userRooms;
        notifyDataSetChanged();
    }

    public void clearUsers() {
        userRooms.clear();
        notifyDataSetChanged();
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
        holder.bind(userRoom,context);
    }

    @Override
    public int getItemCount() {
        return userRooms.size();
    }

    public void addUser(UserRoom userRoom) {
        if (userRoom.isAdmin()) {
            userRooms.add(0, userRoom);
            notifyItemInserted(0);

            for (int i = 1; i < userRooms.size(); i++) {
                notifyItemChanged(i);
            }
        } else {
            userRooms.add(userRoom);
            notifyItemInserted(userRooms.size());
        }
    }

    public void removeUser(String uuid) {
        UserRoom userRoom = userRooms.stream().filter(u -> u.getUUID().equals(uuid)).findFirst().orElse(null);
        int index = userRooms.indexOf(userRoom);
        System.out.println("Index: " + index);
        System.out.println("Users:" + userRooms);
        userRooms.remove(userRoom);
        notifyItemRemoved(index);
    }

    public void updateUser(UserRoom userRoom) {
        UserRoom oldUserRoom = userRooms.stream().filter(u -> u.getUUID().equals(userRoom.getUUID())).findFirst().orElse(null);
        int index = userRooms.indexOf(oldUserRoom);
        userRooms.set(index, userRoom);
        notifyItemChanged(index);

        /*
        *   UserRoom oldUserRoom = userRooms.stream()
            .filter(u -> u.getUUID().equals(userRoom.getUUID()))
            .findFirst()
            .orElse(null);

    if (oldUserRoom != null) { // Verificar si el usuario existe
        int index = userRooms.indexOf(oldUserRoom);

        if (userRoom.isAdmin()) { // Si el usuario actualizado ahora es administrador
            // Eliminar el usuario de su posición actual
            userRooms.remove(index);
            notifyItemRemoved(index);

            // Insertarlo en la posición 0
            userRooms.add(0, userRoom);
            notifyItemInserted(0);

            // Notificar cambios en el resto de las posiciones
            for (int i = 1; i < userRooms.size(); i++) {
                notifyItemChanged(i);
            }
        } else { // Si no es administrador
            userRooms.set(index, userRoom);
            notifyItemChanged(index);
        }
    }*/
    }
}
