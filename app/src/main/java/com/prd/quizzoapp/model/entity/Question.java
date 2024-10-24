package com.prd.quizzoapp.model.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String uuid;
    private String question;
    private List<QuestionOption> options;

    public Question() {
    }

    public Question(String uuid, String question, List<QuestionOption> options) {
        this.uuid = uuid;
        this.question = question;
        this.options = options;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @NonNull
    @Override
    public String toString() {
        return "Question{" +
                "uuid='" + uuid + '\'' +
                ", question='" + question + '\'' +
                ", options=" + options.size() +
                '}';
    }
}
