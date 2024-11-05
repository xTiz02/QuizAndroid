package com.prd.quizzoapp.model.service.intf;

import com.prd.quizzoapp.model.dto.QuizRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetroApiService {

    @POST("api/{roomUUID}/send")
    Call<String> getQuestions(@Body QuizRequestDto quizRequestDto,@Path("roomUUID") String roomUUID);

    @GET("sse/{roomUUID}/delete")
    Call<String> deleteRoomSse(@Path("roomUUID") String roomUUID);



    /*
    * @Headers(value = "Content-Type: application/json")
@POST("api/Persona/Add")
Call<Persona> AddPersona(@Header("authorization") String token, @Body JsonObject object);
 JsonObject postParam = new JsonObject();
       postParam.addProperty("PersonaCedula", item.getPersonaCedula());*/
}
