package com.prd.quizzoapp.model.service.intf;

public interface ActionCallback {
    void onSuccess();
    void onFailure(Exception e);
}
