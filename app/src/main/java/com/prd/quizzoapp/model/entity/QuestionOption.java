package com.prd.quizzoapp.model.entity;

import java.io.Serializable;

public class QuestionOption implements Serializable {
    private String uuid;
    private String option;
    private boolean correct;

    public QuestionOption(String uuid, String option, boolean correct) {
        this.uuid = uuid;
        this.option = option;
        this.correct = correct;
    }

    public QuestionOption() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "QuestionOption{" +
                "uuid='" + uuid + '\'' +
                ", option='" + option + '\'' +
                ", correct=" + correct +
                '}';
    }
}
