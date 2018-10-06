package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;


public class PersistingCreditCardServlet extends HttpServlet {

    private static final String JSON_POST_KEY = "credit-card";

    private final Logger logger = Logger.getLogger(getClass());

    private CreditCardService creditCardService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("received path " + req.getServletPath());
        }

        String normalizedPath = Paths.get(req.getServletPath()).normalize().toString();

        String[] split = normalizedPath.split("/");


        if (!"POST".equals(req.getMethod())) {
            logger.warn("bad request method " + req.getMethod() + ", expecting POST");

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        if (split.length != 2) {
            logger.warn("bad POST path request " + req.getPathInfo() + " (normalized path: " + normalizedPath + ")");

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        try (OutputStream out = resp.getOutputStream()) {
            try (InputStream in = req.getInputStream()) {
                String json = CharStreams.toString(new InputStreamReader(in));

                Map<String, String> creditCardMap = objectMapper.readValue(json, Map.class);

                if (creditCardMap.size() != 1 || !creditCardMap.containsKey(JSON_POST_KEY)) {
                    logger.warn("received invalid json " + json);

                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                    return;
                }

                String token = creditCardService.persist(creditCardMap.get(JSON_POST_KEY));

                String jsonRes = "{\"token\" : \"" + token + "\"}";

                out.write(jsonRes.getBytes(UTF_8));

                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }

    }

    @Required
    public void setCreditCardService(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }
}
