package com.prd.quizzoapp.model.entity;

public class User {
    private String UUID;
    private String username;
    private String email;
    private String password;
    private String description;
    private String img;

    public User(String UUID, String username, String email, String password, String description, String img) {
        this.UUID = UUID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.description = description;
        this.img = img;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
