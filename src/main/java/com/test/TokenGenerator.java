package com.test;

import com.google.common.base.Joiner;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class TokenGenerator {

    private static final SecureRandom random = new SecureRandom();

    private TokenGenerator() {
    }

    public static String generate() {
        int tokenPartsSize = 5;
        List<String> tokenParts = new ArrayList<>(tokenPartsSize);
        for (int i = 0; i < tokenPartsSize; i++) {
            tokenParts.add(Integer.toHexString(random.nextInt()));
        }

        return Joiner.on('-').join(tokenParts);
    }
}
