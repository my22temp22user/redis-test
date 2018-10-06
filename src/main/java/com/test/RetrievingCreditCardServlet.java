package com.test;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import static com.google.common.base.Charsets.UTF_8;


public class RetrievingCreditCardServlet extends HttpServlet {

    private final Logger logger = Logger.getLogger(getClass());

    private CreditCardService creditCardService;


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("received path " + req.getPathInfo());
        }

        if (!"GET".equals(req.getMethod())) {
            logger.warn("bad request method " + req.getMethod() + ", expecting GET");

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        String normalizedPath = Paths.get(req.getPathInfo()).normalize().toString();

        String[] split = normalizedPath.split("/");

        if (split.length != 2) {

            logger.warn("bad GET path request " + req.getPathInfo() + " (normalized path: " + normalizedPath + ")");

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        try (OutputStream out = resp.getOutputStream()) {

            String token = split[1];

            String creditCard = creditCardService.getCreditCardByToken(token);

            if (creditCard == null) {
                logger.warn("bad request for not existing credit card " + token);

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                return;
            }

            String jsonRes = "{\"credit-card\":\"" + creditCard + "\"}";

            out.write(jsonRes.getBytes(UTF_8));
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Required
    public void setCreditCardService(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }
}
