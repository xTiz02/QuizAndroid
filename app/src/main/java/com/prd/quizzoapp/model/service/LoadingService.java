package com.prd.quizzoapp.model.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.prd.quizzoapp.R;

public class LoadingService {

    private Context context;
    private Dialog alertDialog;

    public LoadingService(Context context) {
        this.context = context;
    }

    public void showLoading(String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.progress_bar)
                .setCancelable(false)
                .create();
        alertDialog.show();
        TextView tv =  alertDialog.findViewById(R.id.tv_loading);
        tv.setText(message);
    }

    public void updateMessage(String message) {
        TextView tv =  alertDialog.findViewById(R.id.tv_loading);
        tv.setText(message);
    }

    public void hideLoading() {
        alertDialog.dismiss();
    }

    /*public void showLoadingWithMessage(String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.progress_bar)
                .setMessage(message)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    public void hideLoadingWithMessage() {
        alertDialog.dismiss();
    }

    public void showLoadingWithMessageAndTitle(String title, String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.progress_bar)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    public void hideLoadingWithMessageAndTitle() {
        alertDialog.dismiss();
    }

    public void showLoadingWithMessageAndTitleAndIcon(String title, String message, int icon) {
        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.progress_bar)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    public void hideLoadingWithMessageAndTitleAndIcon() {
        alertDialog.dismiss();
    }

    public void showLoadingWithMessageAndTitleAndIconAndButton(String title, String message, int icon, String button) {
        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.progress_bar)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton(button, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    public void hideLoadingWithMessageAndTitleAndIconAndButton() {
        alertDialog.dismiss();
    }*/
}
