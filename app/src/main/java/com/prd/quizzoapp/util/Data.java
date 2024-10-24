package com.prd.quizzoapp.util;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.model.entity.Category;
import com.prd.quizzoapp.model.entity.Question;
import com.prd.quizzoapp.model.entity.QuestionOption;
import com.prd.quizzoapp.model.entity.SubCategory;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.entity.UserResult;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static ArrayList<Category> getCategories() {
        return new ArrayList<>(List.of(
                new Category(CategoryEnum.DRAMA, R.drawable.drama,false),
                new Category(CategoryEnum.CIENCIA, R.drawable.science,false),
                new Category(CategoryEnum.PUZZLE, R.drawable.puzzle,false),
                new Category(CategoryEnum.HISTORIA, R.drawable.history,false),
                new Category(CategoryEnum.MATEMATICAS, R.drawable.mathematics,false),
                new Category(CategoryEnum.LENGUAJE, R.drawable.language,false),
                new Category(CategoryEnum.TECNOLOGIA, R.drawable.tech,false),
                new Category(CategoryEnum.RANDOM, R.drawable.random,false)
        )
        );

    }

    public static ArrayList<SubCategory> getSubCategories() {
        return new ArrayList<>(List.of(
                new SubCategory("Romeo y Julieta", CategoryEnum.DRAMA),

                new SubCategory("Web", CategoryEnum.TECNOLOGIA),
                new SubCategory("Android", CategoryEnum.TECNOLOGIA),
                new SubCategory("Frameworks", CategoryEnum.TECNOLOGIA),
                new SubCategory("Java", CategoryEnum.TECNOLOGIA),
                new SubCategory("Python", CategoryEnum.TECNOLOGIA),

                new SubCategory("Ortografia", CategoryEnum.LENGUAJE),

                new SubCategory("Guerras Mundiales", CategoryEnum.HISTORIA),


                new SubCategory("Fisica", CategoryEnum.CIENCIA),
                new SubCategory("Quimica", CategoryEnum.CIENCIA),

                new SubCategory("Algebra", CategoryEnum.MATEMATICAS),
                new SubCategory("Calculo", CategoryEnum.MATEMATICAS)));
    }

    public static ArrayList<User> getUsers() {
        return new ArrayList<>(List.of(
                new User("1", "user1", "Hola! Me gusta este juego 😒 Hola! me gusta este juego", R.drawable.profile_pic,true),
                new User("2", "user2", "Hola! Me gusta este juego", R.drawable.profile_pic,false),
                new User("3", "user3", "Hola! Me gusta este juego", R.drawable.profile_pic,false),
                new User("4", "user4", "Hola! Me gusta este juego", R.drawable.profile_pic,false),
                new User("5", "user5", "Hola! Me gusta este juego", R.drawable.profile_pic,false),
                new User("6", "user6", "Hola! Me gusta este juego", R.drawable.profile_pic,false)));
    }

    public static ArrayList<Question> getQuestions(){
        return new ArrayList<>(List.of(
                new Question("1","¿Cuál es el país más grande del mundo?",
                        new ArrayList<>(List.of(
                                new QuestionOption("1","Rusia",true),
                                new QuestionOption("2","China",false),
                                new QuestionOption("3","Estados Unidos",false),
                                new QuestionOption("4","Canadá",false)))),
                new Question("2","¿Cuál es el país más pequeño del mundo?",
                        new ArrayList<>(List.of(
                                new QuestionOption("1","Vaticano",true),
                                new QuestionOption("2","Mónaco",false),
                                new QuestionOption("3","Nauru",false),
                                new QuestionOption("4","San Marino",false)))),
                new Question("3","¿Cuál es el río más largo del mundo?",
                        new ArrayList<>(List.of(
                                new QuestionOption("1","Amazonas",true),
                                new QuestionOption("2","Nilo",false),
                                new QuestionOption("3","Yangtsé",false),
                                new QuestionOption("4","Misisipi",false))))
                ));

    }

    public SubCategory getSubCategoryByName(String name) {
        for (SubCategory subCategory : getSubCategories()) {
            if (subCategory.getName().equals(name)) {
                return subCategory;
            }
        }

        return new SubCategory(name, CategoryEnum.RANDOM);
    }

    public static ArrayList<UserResult> getUserResults() {
        return new ArrayList<>(List.of(
                new UserResult("1", 100, "user1", R.drawable.profile_pic,5 ,0,10.5,true),
                new UserResult("2", 90, "user2", R.drawable.profile_pic,4 ,1,10.5,true),
                new UserResult("3", 80, "user3", R.drawable.profile_pic,3 ,2,10.5,true),
                new UserResult("4", 70, "user4", R.drawable.profile_pic,2 ,3,10.5,true),
                new UserResult("5", 60, "user5", R.drawable.profile_pic,1 ,4,10.5,true),
                new UserResult("6", 50, "user6", R.drawable.profile_pic,0 ,5,10.5,false)));
    }



    /*
    * // Generar un UUID aleatorio
        UUID uniqueID = UUID.randomUUID();

        // Convertir el UUID a cadena (String) si lo deseas usar como ID de usuario
        String userId = uniqueID.toString();*/
}
