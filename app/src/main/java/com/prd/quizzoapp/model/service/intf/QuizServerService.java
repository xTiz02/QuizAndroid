package com.prd.quizzoapp.model.service.intf;

import com.prd.quizzoapp.model.dto.QuizRequestDto;

public interface QuizServerService {

    public void generateQuestions(QuizRequestDto quizRequestDto,String roomUUID, ActionCallback callback);
    public void deleteRoomSse(String roomUUID, ActionCallback callback);
}
