package com.sqa.helpers;

import com.sqa.model.Issue;

public interface IDataHelperIssue {
    public Issue createIssue(Issue issue);
    public Issue createIssue();
    public Issue getIssue(Integer issueNumber);
    public void deleteIssue(Integer issueNumber);
    public Issue updateIssue(Issue issue);
}
