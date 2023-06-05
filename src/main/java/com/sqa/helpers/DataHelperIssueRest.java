package com.sqa.helpers;

import com.sqa.model.Issue;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class DataHelperIssueRest implements IDataHelperIssue{
    private RequestSpecification restAssuredGitHub;
    private static final String BASE_URI = "https://api.github.com";

    public DataHelperIssueRest() {
        this.restAssuredGitHub = given().spec(Specification.requestSpecificationForIssues()).baseUri(BASE_URI);
    }

    @Override
    public Issue createIssue(Issue issue) {
        return this.restAssuredGitHub.body(issue).when().post().body().as(Issue.class);
    }

    @Override
    public Issue createIssue() {
        return this.createIssue(new Issue());
    }

    @Override
    public Issue getIssue(Integer issueNumber) {
        return this.restAssuredGitHub.when().get(issueNumber.toString()).body().as(Issue.class);
    }

    @Override
    public void deleteIssue(Integer issueNumber) {
        this.restAssuredGitHub.when().delete(issueNumber.toString());
    }

    @Override
    public Issue updateIssue(Issue issue) {
        return this.restAssuredGitHub.body(issue).when().post(issue.getNumber().toString()).body().as(Issue.class);
    }
}
