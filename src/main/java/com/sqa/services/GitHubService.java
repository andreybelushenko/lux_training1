package com.sqa.services;

import com.sqa.model.Issue;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface GitHubService {
    @GET("zen")
    Call<String> getZen();

    @GET("repos/{accountName}/{repoName}/issues")
    Call<String> getIssuesNoAuth(@Path("accountName") String accountName,
                                 @Path("repoName") String repoName);

    @GET("repos/{accountName}/{repoName}/issues")
    Call<String> getIssuesWithAuth(@Path("accountName") String accountName,
                                   @Path("repoName") String repoName,
                                   @Header("Authorization") String bearerToken);

    @GET("repos/{accountName}/{repoName}/issues")
    Call<String> getIssuesWithAuthWithAccept(@Path("accountName") String accountName,
                                   @Path("repoName") String repoName,
                                   @Header("Authorization") String bearerToken,
                                   @Header("Accept") String accept);

    @POST("repos/{accountName}/{repoName}/issues")
    Call<Issue> postIssue(@Path("accountName") String accountName,
                          @Path("repoName") String repoName,
                          @Header("Authorization") String bearerToken,
                          @Header("Accept") String accept,
                          @Body Issue issue);

    @POST("repos/{accountName}/{repoName}/issues")
    Call<Map<String, Object>> postIssue(@Path("accountName") String accountName,
                                        @Path("repoName") String repoName,
                                        @Header("Authorization") String bearerToken,
                                        @Header("Accept") String accept,
                                        @Body Map<String, Object> issue);

}
