package com.prd.quizzoapp.views.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prd.quizzoapp.databinding.SenderLayoutBinding;
import com.prd.quizzoapp.model.entity.UserMessage;
import com.squareup.picasso.Picasso;

public class SenderViewHolder extends RecyclerView.ViewHolder {

    private final SenderLayoutBinding binding;
    public SenderViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = SenderLayoutBinding.bind(itemView);
    }

    public void bind(UserMessage messages,String img, Context context) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Eliminar")
                        .setMessage("¿Estás seguro de que quieres eliminar este mensaje?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        binding.msgsendertyp.setText(messages.getMessage());
        Picasso.get().load(img).into(binding.profilerggg);
    }
}
