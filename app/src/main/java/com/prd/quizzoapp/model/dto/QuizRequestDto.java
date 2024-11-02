package com.prd.quizzoapp.model.dto;

import java.util.ArrayList;

public class QuizRequestDto {
    private int numQuestions;
    private ArrayList<String> categories;
    private ArrayList<String> subcategories;

    public QuizRequestDto() {
    }

    public QuizRequestDto(int numQuestions, ArrayList<String> subcategories, ArrayList<String> categories) {
        this.numQuestions = numQuestions;
        this.subcategories = subcategories;
        this.categories = categories;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public ArrayList<String> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<String> subcategories) {
        this.subcategories = subcategories;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
