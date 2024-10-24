package com.prd.quizzoapp.model.entity;

public class Score {
    private String questionUuid;
    private double time;
    private boolean markedCorrect;
    private double score;

    public Score(String questionUuid, double score, double time,boolean markedCorrect) {
        this.questionUuid = questionUuid;
        this.score = score;
        this.time = time;
        this.markedCorrect = markedCorrect;
    }

    public Score() {
    }

    public boolean isMarkedCorrect() {
        return markedCorrect;
    }

    public void setMarkedCorrect(boolean markedCorrect) {
        this.markedCorrect = markedCorrect;
    }

    public String getQuestionUuid() {
        return questionUuid;
    }

    public void setQuestionUuid(String questionUuid) {
        this.questionUuid = questionUuid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Score{" +
                "questionUuid='" + questionUuid + '\'' +
                ", time=" + time +
                ", score=" + score +
                ", markedCorrect=" + markedCorrect +
                '}';
    }
}
