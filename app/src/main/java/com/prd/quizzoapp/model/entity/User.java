package com.prd.quizzoapp.model.entity;

public class User {
    private String UUID;
    private String username;
    private String description;
    private int img;
    private boolean isAdmin;

    public User(String uuid,String username, String description, int img, boolean isAdmin) {
        UUID = uuid;
        this.username = username;
        this.description = description;
        this.img = img;
        this.isAdmin = isAdmin;
    }
    public User() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "UUID='" + UUID + '\'' +
                "username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", img=" + img +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
