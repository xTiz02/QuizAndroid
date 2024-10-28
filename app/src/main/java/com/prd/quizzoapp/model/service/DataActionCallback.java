package com.prd.quizzoapp.model.service;

public interface DataActionCallback<T> {
    void onSuccess(T user);
    void onFailure(Exception e);
}
