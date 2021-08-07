package com.sastix.cms.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@TestInstance(Lifecycle.PER_CLASS)
@EnabledIfSystemProperty(named = "integration.tests.enabled", matches = "true")
public class AuthenticationTests {
    
    CloseableHttpClient httpclient = HttpClients.createMinimal();

    Properties properties = new Properties();

    @BeforeAll
    void readProperties() throws IOException{
        try (FileInputStream is = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(is);
        }
    }

    @Test
    void testUnauthenticatedRequestRedirectsToKeycloak() throws ClientProtocolException, IOException{
        HttpGet httpget = new HttpGet(properties.getProperty("cms.server.url") + "/apiVersion");
        CloseableHttpResponse httpresponse = httpclient.execute(httpget);
        assertEquals(302, httpresponse.getStatusLine().getStatusCode());
    }

    @Test
    void authenticatedRequestReturnsSuccessfully() throws ClientProtocolException, IOException{
        String token = getAccessToken();
        HttpGet httpget = new HttpGet(properties.getProperty("cms.server.url") + "/apiversion");
        httpget.addHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse httpresponse = httpclient.execute(httpget);
        assertEquals(200, httpresponse.getStatusLine().getStatusCode());
    }

    String getAccessToken() throws ClientProtocolException, IOException{
        String authServerURL = properties.getProperty("keycloak.auth-server-url");
        String authServerRealm = properties.getProperty("keycloak.realm");
        HttpPost httpPost = new HttpPost(authServerURL + "/realms/" + authServerRealm + "/protocol/openid-connect/token");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", properties.getProperty("keycloak.users.admin.username")));
        params.add(new BasicNameValuePair("password", properties.getProperty("keycloak.users.admin.password")));
        params.add(new BasicNameValuePair("client_id", properties.getProperty("keycloak.resource")));
        params.add(new BasicNameValuePair("client_secret", properties.getProperty("keycloak.credentials.secret")));
        params.add(new BasicNameValuePair("grant_type", "password"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(EntityUtils.toString(httpResponse.getEntity()));
        return node.get("access_token").asText();
    }
}
