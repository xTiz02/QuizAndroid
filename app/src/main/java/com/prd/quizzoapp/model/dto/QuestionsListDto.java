package com.prd.quizzoapp.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prd.quizzoapp.model.entity.Question;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionsListDto implements Serializable {
    @SerializedName("questions_list")
    @Expose
    public ArrayList<Question> questions;

    public QuestionsListDto() {
    }

    public QuestionsListDto(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuestionsListDto{" +
                "questions=" + questions +
                '}';
    }
}
