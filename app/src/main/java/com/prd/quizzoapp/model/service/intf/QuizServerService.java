package com.prd.quizzoapp.model.service.intf;

import com.prd.quizzoapp.model.dto.QuizRequestDto;

public interface QuizServerService {

    public void generateQuestions(QuizRequestDto quizRequestDto,String roomUUID, ActionCallback callback);
    public void connectToSseServer(String roomUUID, DataActionCallback<String> callback);
    public void disconnectSseServer();
    public void deleteRoomSse(String roomUUID, ActionCallback callback);
}
