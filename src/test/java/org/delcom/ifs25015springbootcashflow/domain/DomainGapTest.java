package org.delcom.ifs25015springbootcashflow.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** Menutup cabang catch di PasswordHasher.hash (SHA-256 tidak tersedia). */
class DomainGapTest {

    @Test
    void hashThrowsIllegalStateWhenSha256Missing() {
        try (MockedStatic<MessageDigest> md = mockStatic(MessageDigest.class)) {
            md.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("tidak ada"));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> PasswordHasher.hash("rahasia"));

            assertEquals("SHA-256 tidak tersedia", ex.getMessage());
            assertInstanceOf(NoSuchAlgorithmException.class, ex.getCause());
        }
    }
}
