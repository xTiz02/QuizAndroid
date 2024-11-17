package com.prd.quizzoapp.model.entity;

public class UserRoom {
    private String UUID;
    private String username;
    private String description;
    private String img;
    private boolean playing;
    private boolean isAdmin;

    public UserRoom(String uuid, String username, String description, String img, boolean playing, boolean isAdmin) {
        UUID = uuid;
        this.username = username;
        this.playing = playing;
        this.description = description;
        this.img = img;
        this.isAdmin = isAdmin;
    }


    public UserRoom() {
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
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
        return "UserRoom{" +
                "UUID='" + UUID + '\'' +
                "username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", img=" + img +
                ", isAdmin=" + isAdmin +
                ", playing=" + playing +
                '}';
    }
}
