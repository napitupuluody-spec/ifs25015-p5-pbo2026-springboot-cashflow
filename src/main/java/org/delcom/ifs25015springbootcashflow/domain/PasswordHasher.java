package org.delcom.ifs25015springbootcashflow.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Hash dan verifikasi password memakai SHA-256 (hex lowercase). */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String raw) {
        return digest(raw, "SHA-256");
    }

    static String digest(String raw, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " tidak tersedia", e);
        }
    }

    public static boolean verify(String raw, String hash) {
        if (raw == null || hash == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(raw).getBytes(StandardCharsets.UTF_8),
                hash.getBytes(StandardCharsets.UTF_8));
    }
}
