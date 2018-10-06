package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.InputStreamEntity;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.test.Constants.APPLICATION_CONTEXT_NAME;
import static com.test.Constants.SERVER_PORT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ServerIT {

    private static final String APPLICATION_CONTEXT_TEST_NAME = "applicationContext-test.xml";
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;

    private final Logger logger = Logger.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private int port;


    @Before
    public void before() throws Throwable {
        System.setProperty(APPLICATION_CONTEXT_NAME, APPLICATION_CONTEXT_TEST_NAME);

        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(policy);

        retryTemplate.execute((RetryCallback<Object, Throwable>) context -> {
            startServer();
            return null;
        });


        sleepUninterruptibly(3, SECONDS);
    }

    private void startServer() throws Exception {
        port = generatePort();
        System.setProperty(SERVER_PORT, String.valueOf(port));
        Main.main(new String[0]);
    }

    @Test
    public void test() throws Exception {
        String creditCardPrefix = "credit_card_";

        int numOfCreditCardsToTest = 100;
        List<String> tokens = new ArrayList<>(numOfCreditCardsToTest);

        for (int i = 0; i < numOfCreditCardsToTest; i++) {
            tokens.add(testCreditCardAndGetToken(creditCardPrefix + i));
        }

        for (int i = 0; i < numOfCreditCardsToTest; i++) {
            HttpResult creditCardRes = getCreditCardByToken(tokens.get(i));
            assertEquals(OK, creditCardRes.statusCode);

            Map<String, String> creditCardMap = objectMapper.readValue(creditCardRes.content, Map.class);
            assertEquals(ImmutableMap.of("credit-card", creditCardPrefix+i), creditCardMap);
        }

        HttpResult nonExistingRes = getNotExistingCreditCard("not_existing_token");
        assertEquals(BAD_REQUEST, nonExistingRes.statusCode);
    }

    private String testCreditCardAndGetToken(String creditCard) throws IOException {
        HttpResult tokenRes = persist(creditCard);

        Map<String, String> tokenMap = objectMapper.readValue(tokenRes.content, Map.class);

        assertEquals(1, tokenMap.size());
        assertTrue(tokenMap.containsKey("token"));

        String token = tokenMap.get("token");
        logger.info("for credit card " + creditCard + " token is " + token);

        HttpResult creditCardRes = getCreditCardByToken(token);

        assertEquals(OK, creditCardRes.statusCode);

        Map<String, String> creditCardMap = objectMapper.readValue(creditCardRes.content, Map.class);
        assertEquals(ImmutableMap.of("credit-card", creditCard), creditCardMap);

        return token;
    }

    private HttpResult persist(String creditCard) throws IOException {
        return executeHttpPostRequest("/creditcard", creditCard);
    }
    private HttpResult getCreditCardByToken(String token) throws IOException {
        return executeHttpGetRequest("/creditcard/"+token);
    }

    private HttpResult getNotExistingCreditCard(String notExistingToken) throws IOException {
        return executeHttpGetRequest("/creditcard/"+notExistingToken);
    }

    private HttpResult executeHttpPostRequest(String path, String creditCard) throws IOException {
        return executeHttpRequest(path, url -> {
            String json = "{\"credit-card\":\""+creditCard+"\"}";

            return Request.Post(url)
                    .body(new InputStreamEntity(new ByteArrayInputStream(json.getBytes(Charsets.UTF_8))));
        });
    }

    private HttpResult executeHttpGetRequest(String path) throws IOException {
        return executeHttpRequest(path, Request::Get);
    }

    private HttpResult executeHttpRequest(String path, Function<String, Request> requestProvider) throws IOException {
        String url = "http://localhost:" + port + path;

        logger.info("executing http request: " + url);

        Request request = requestProvider.apply(url);

        HttpResponse response = request.execute().returnResponse();

        return new HttpResult(response.getStatusLine().getStatusCode(), CharStreams.toString(new InputStreamReader(response.getEntity().getContent())));
    }

    private int generatePort() {
        int port = random.nextInt(65535 - 1024) + 1024;

        logger.info("generated port: " + port);

        return port;
    }

    private static class HttpResult {
        private final int statusCode;
        private final String content;

        private HttpResult(int statusCode, String content) {
            this.statusCode = statusCode;
            this.content = content;
        }
    }
}
