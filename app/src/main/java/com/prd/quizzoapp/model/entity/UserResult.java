package com.prd.quizzoapp.model.entity;

import androidx.annotation.NonNull;

public class UserResult {
    private String userUuid;
    private double score;
    private String username;
    private String img;
    private int correctAnswers;
    private int wrongAnswers;
    private double time;
    private boolean played;

    public UserResult() {
    }

    public UserResult(String userUuid, double score, String username, String img, int correctAnswers, int wrongAnswers,double time, boolean played) {
        this.userUuid = userUuid;
        this.score = score;
        this.username = username;
        this.img = img;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.played = played;
        this.time = time;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserResult{" +
                "userUuid='" + userUuid + '\'' +
                ", score=" + score +
                ", username='" + username + '\'' +
                ", img=" + img +
                ", correctAnswers=" + correctAnswers +
                ", wrongAnswers=" + wrongAnswers +
                ", played=" + played +
                ", time=" + time +
                '}';
    }
}
