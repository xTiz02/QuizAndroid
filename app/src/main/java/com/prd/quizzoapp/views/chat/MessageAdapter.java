package com.prd.quizzoapp.views.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.prd.quizzoapp.R;
import com.prd.quizzoapp.model.entity.UserMessage;
import com.prd.quizzoapp.util.Util;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    List<UserMessage> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;
    private String img;

    public MessageAdapter(Context context, List<UserMessage> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//2
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new ReciveViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {//3
        UserMessage messages = messagesAdpterArrayList.get(position);
        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).bind(messages,img,context);
        }else if(holder.getClass() == ReciveViewHolder.class){
            ((ReciveViewHolder) holder).bind(messages,img,context);
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {//1
        UserMessage messages = messagesAdpterArrayList.get(position);
        img = Util.getImages().get(messages.getSenderId());
        if (messages.getSenderId().equals(FirebaseAuth.getInstance().getUid().toString())){
            return ITEM_SEND;
        }else {
            return ITEM_RECIVE;
        }
    }

    public void addMessage(UserMessage msgModelClass) {
        messagesAdpterArrayList.add(msgModelClass);
        notifyDataSetChanged();
    }
    public void clearMessages(){
        messagesAdpterArrayList.clear();
        notifyDataSetChanged();
    }
}
