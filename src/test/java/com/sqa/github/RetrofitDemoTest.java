package com.sqa.github;

import com.sqa.model.Issue;
import com.sqa.services.GitHubService;
import com.sqa.services.GorestService;
import com.sqa.utils.TestLogger;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class RetrofitDemoTest implements TestLogger {

    private Retrofit retrofitGit;
    private Retrofit retrofitGorest;
    private static final String GIT_HUB_URL = "https://api.github.com/";
    private static final String GOREST_URL = "https://gorest.co.in/";

    private String issueTitle = String.format("issue %s", RandomStringUtils.randomAlphabetic(5));
    private String issueDescription = "Description of new issue";

    private GitHubService gitHubService;
    private GorestService gorestService;

    public RetrofitDemoTest() {
        OkHttpClient client = configureToIgnoreCertificate(new OkHttpClient.Builder()).build();

        this.retrofitGit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GIT_HUB_URL)
                .build();
        this.gitHubService = retrofitGit.create(GitHubService.class);

        this.retrofitGorest = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(GOREST_URL)
                .build();

        this.gorestService = retrofitGorest.create(GorestService.class);
    }
    private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {

        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {

        }
        return builder;
    }
    /*
        01. Check that 200 code comes in response to a simple GET
    */
    @Test
    public void verifyHealthcheckTest() throws IOException {
        Response<String> response = this.gitHubService.getZen().execute();
        Assertions.assertEquals(response.code(), 200);
    }

    /*
        02. Check that a non-empty response body to a simple GET is coming
    */
    @Test
    public void verifyDefunktBodyTest() throws IOException {
        Response<String> response = this.gitHubService.getZen().execute();
        Assertions.assertFalse(response.body().isEmpty());
    }

    /*
        03. Check that the answer body contains a field equal to the value
    */
    @Test
    public void verifyIssuesContainTest() throws IOException {
        Response<String> response = this.gitHubService.getIssuesNoAuth(
                        "musagulov",
                        "sqa")
                .execute();
        Assertions.assertTrue(response.errorBody().string().contains("Not Found"));
    }

    /*
        04. Check that the response body contains a field after authorization
    */
    @Test
    public void verifyIssuesAuthorized() throws IOException {
        Response<String> response = this.gitHubService.getIssuesWithAuth(
                        "musagulov",
                        "sqa",
                        "Bearer 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")
                .execute();
        Assertions.assertTrue(response.body().contains("issue wSgnl"), "response not contain specific title");
    }

    /*
        05. Check that the response body contains an error and a 403 code
    */
    @Test
    public void verifyIssuesNoUserAgent() throws IOException {
        Response<String> response = this.gitHubService.getIssuesWithAuthWithAccept(
                        "musagulov",
                        "sqa",
                        "Bearer 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",
                        "Application/XML")
                .execute();
        Assertions.assertTrue(response.errorBody().string().contains("Unsupported 'Accept' header"), "response not contain \"Unsupported 'Accept' header\"");
    }

    /*
        06. Checking that the issue is published
    */
    @Test
    public void verifyPostIssues() {
        
    }

    /*
        07. Checking that the issue is published (data in url param)
    */
    @Test
    public void verifyPostIssuesUrlParam() throws IOException {
        Response<String> response = this.gorestService.postPostUrl(
                "Bearer 6a2e66915f5232398603c71eda843f6076c46a853840ec5046ae6b7190db7f36",
                "2558095",
                issueTitle,
                 "test body").execute();
        Assertions.assertEquals(201, response.code());
        Assertions.assertTrue(response.body().contains(issueTitle));
    }

    /*
        08. Checking that the issue is published  (POJO)
    */
    @Test
    public void verifyPostPojo() throws IOException {
        // Arrange
        Issue requestIssue = new Issue()
                .setTitle(issueTitle)
                .setBody("some Body");

        //Act
        Response<Issue> issueResponse = this.gitHubService.postIssue("musagulov",
                "sqa",
                "Bearer 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",
                "Application/json", requestIssue).execute();

        //Assert
        Assertions.assertAll(() -> {
            Assertions.assertEquals(requestIssue,issueResponse.body());
            Assertions.assertEquals("open", issueResponse.body().getState());
        });
    }

    /*
        09. Checking that the issue is published  (Map)
    */
    @Test
    public void verifyPostMap() throws IOException {
        Map<String, Object> requestIssueMap = new HashMap<>();
        requestIssueMap.put("title", issueTitle);
        requestIssueMap.put("body", "some body");

        Response<Map<String, Object>> responseIssue = this.gitHubService.postIssue("musagulov",
                "sqa",
                "Bearer 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",
                "Application/json", requestIssueMap).execute();


        Assertions.assertAll(() -> {
            Assertions.assertEquals(requestIssueMap.get("title"), responseIssue.body().get("title"));
            Assertions.assertEquals("open", responseIssue.body().get("state"));
        });
    }

}
