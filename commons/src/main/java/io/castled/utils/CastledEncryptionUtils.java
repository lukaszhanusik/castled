package io.castled.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CastledEncryptionUtils {

    public static String toSHA256String(String str) {
        MessageDigest digest = getSHA256MessageDigest();
        byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static MessageDigest getSHA256MessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Missing SHA-256 algorithm implementation.", e);
        }
    }
}
