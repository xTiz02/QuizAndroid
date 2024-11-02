package com.prd.quizzoapp.model.service.intf;

import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.entity.Question;

import java.util.List;

public interface QuizServerService {

    public void generateQuestions(QuizRequestDto quizRequestDto, DataActionCallback<List<Question>> callback);
}
