package com.prd.quizzoapp.model.service;

public interface DataActionCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
