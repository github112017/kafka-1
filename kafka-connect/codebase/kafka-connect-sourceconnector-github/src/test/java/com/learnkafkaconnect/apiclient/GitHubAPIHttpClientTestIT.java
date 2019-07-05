package com.learnkafkaconnect.apiclient;

import com.learnkafkaconnect.config.GitHubSourceConnectorConfig;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GitHubAPIHttpClientTestIT {

    GitHubSourceConnectorConfig gitHubSourceConnectorConfig;
    GitHubAPIHttpClient gitHubAPIHttpClient;

    @Before
    public void setUp(){

        Map<String, String> inputMap = new HashMap();
        inputMap.put("github.owner","kubernetes" );
        inputMap.put("github.repo","kubernetes" );
        inputMap.put("topic","githubIssues" );
        gitHubSourceConnectorConfig = new GitHubSourceConnectorConfig(inputMap);
        gitHubAPIHttpClient = new GitHubAPIHttpClient(gitHubSourceConnectorConfig);
    }

    @Test
    public void getNextIssues() throws InterruptedException {

        //given
        int page = 1;

        Instant dateSince = Instant.parse("2017-01-01T00:00:00Z");

        //when
        JSONArray jsonArray = gitHubAPIHttpClient.getNextIssues(page,dateSince);

        //then
        assertEquals(2,jsonArray.length());
    }



}
