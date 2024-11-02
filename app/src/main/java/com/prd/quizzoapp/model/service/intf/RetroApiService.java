package com.prd.quizzoapp.model.service.intf;

import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.entity.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetroApiService {

    @POST("chat")
    Call<List<Question>> getQuestions(@Body QuizRequestDto quizRequestDto);



    /*
    * @Headers(value = "Content-Type: application/json")
@POST("api/Persona/Add")
Call<Persona> AddPersona(@Header("authorization") String token, @Body JsonObject object);
 JsonObject postParam = new JsonObject();
       postParam.addProperty("PersonaCedula", item.getPersonaCedula());*/
}
