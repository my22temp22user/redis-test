package com.test;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class CreditCardService {

    private final Logger logger = Logger.getLogger(getClass());

    private KeyValueStorage storage;


    public String persist(String creditCard) {
        String token = TokenGenerator.generate();

        if (logger.isInfoEnabled()) {
            logger.info("persisting credit card " + creditCard + " with toke " + token);
        }

        storage.persist(token, creditCard);

        return token;
    }

    public String getCreditCardByToken(String token) {
        return storage.get(token);
    }

    @Required
    public void setStorage(KeyValueStorage storage) {
        this.storage = storage;
    }
}
