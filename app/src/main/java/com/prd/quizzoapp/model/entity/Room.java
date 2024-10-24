package com.prd.quizzoapp.model.entity;

import java.util.ArrayList;

public class Room {
    private String uuid;
    private String code;
    private int questions;
    private int timeOfQuestion;
    private String uuidAdmin;
    private ArrayList<Category> categories;
    private ArrayList<String> subCategories;

    public Room(String uuid, ArrayList<String> subCategories, int timeOfQuestion, ArrayList<Category> categories, String uuidAdmin, int questions, String code) {
        this.uuid = uuid;
        this.subCategories = subCategories;
        this.timeOfQuestion = timeOfQuestion;
        this.categories = categories;
        this.uuidAdmin = uuidAdmin;
        this.questions = questions;
        this.code = code;
    }

    public Room() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<String> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
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
        return "Room{" +
                "uuidAdmin='" + uuidAdmin + '\'' +
                ", timeOfQuestion=" + timeOfQuestion +
                ", subCategories=" + subCategories.size() +
                ", categories=" + categories.size() +
                ", questions=" + questions +
                ", code='" + code + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
