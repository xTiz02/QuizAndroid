package com.prd.quizzoapp.util;

public enum CategoryEnum {
    DRAMA("Drama"),
    CIENCIA("Ciencia"),
    PUZZLE("Puzzle"),
    HISTORIA("Historia"),
    MATEMATICAS("Matematicas"),
    LENGUAJE("Lenguaje"),
    TECNOLOGIA("Tecnologia"),
    RANDOM("Random");

    private final String category;

    CategoryEnum(String category) {
        this.category = category;
    }

    public String getName() {
        return category;
    }
}
