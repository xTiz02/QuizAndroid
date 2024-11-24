package com.prd.quizzoapp.model.entity;

import com.prd.quizzoapp.util.CategoryEnum;

import java.io.Serializable;

public class Category implements Serializable {
    private CategoryEnum category;
    private int img;
    private boolean selected;

    public Category(CategoryEnum category, int img, boolean selected) {
        this.category = category;
        this.img = img;
        this.selected = selected;
    }

    public Category() {
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Category{" +
                "category=" + category +
                ", img=" + img +
                ", selected=" + selected +
                '}';
    }
}
