package com.sqa.services;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GorestService {

    @POST("/public/v1/users/{user}/posts")
    Call<String> postPostUrl(
            @Header("Authorization") String authToken,
            @Path("user") String user,
            @Query("title") String title,
            @Query("body") String body);

}
