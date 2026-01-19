package client;

import java.security.SecureRandom;

public class Generator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }
}
