package com.prd.quizzoapp.model.entity;

import com.prd.quizzoapp.util.CategoryEnum;

import java.io.Serializable;

public class SubCategory implements Serializable {
    private String name;
    private CategoryEnum category;

    public SubCategory(String name, CategoryEnum category) {
        this.name = name;
        this.category = category;
    }

    public SubCategory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "name='" + name + '\'' +
                ", categoryName='" + category + '\'' +
                '}';
    }
}
