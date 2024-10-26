package com.prd.quizzoapp.model.service;

public interface ActionCallback {
    void onSuccess();
    void onFailure(Exception e);
}
