package com.prd.quizzoapp.model.entity;

import java.util.ArrayList;

public class Room {
    private RoomConfig roomConfig;
    private ArrayList<Category> categories;
    private ArrayList<String> subCategories;

    public Room(RoomConfig roomConfig, ArrayList<String> subCategories, ArrayList<Category> categories) {
        this.roomConfig = roomConfig;
        this.subCategories = subCategories;
        this.categories = categories;
    }

    public Room() {
    }

    public RoomConfig getRoomConfig() {
        return roomConfig;
    }

    public void setRoomConfig(RoomConfig roomConfig) {
        this.roomConfig = roomConfig;
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

    @Override
    public String toString() {
        return "Room{" +
                "roomConfig=" + roomConfig +
                ", categories=" + categories.size() +
                ", subCategories=" + subCategories.size() +
                '}';
    }
}
