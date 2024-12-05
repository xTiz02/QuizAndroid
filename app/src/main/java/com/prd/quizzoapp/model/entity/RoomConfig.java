package com.prd.quizzoapp.model.entity;

public class RoomConfig {
    private String uuid;
    private String code;
    private int questions;
    private int timeOfQuestion;
    private String uuidAdmin;
    private int maxPlayers;
    private int currentPlayers;

    public RoomConfig() {
    }

    public RoomConfig(String uuid, String uuidAdmin, int timeOfQuestion, int questions, String code, int maxPlayers, int currentPlayers) {
        this.uuid = uuid;
        this.uuidAdmin = uuidAdmin;
        this.timeOfQuestion = timeOfQuestion;
        this.questions = questions;
        this.code = code;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuidAdmin() {
        return uuidAdmin;
    }

    public void setUuidAdmin(String uuidAdmin) {
        this.uuidAdmin = uuidAdmin;
    }

    public int getTimeOfQuestion() {
        return timeOfQuestion;
    }

    public void setTimeOfQuestion(int timeOfQuestion) {
        this.timeOfQuestion = timeOfQuestion;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "RoomConfig{" +
                "uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                ", questions=" + questions +
                ", timeOfQuestion=" + timeOfQuestion +
                ", uuidAdmin='" + uuidAdmin + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", currentPlayers=" + currentPlayers +
                '}';
    }
}
