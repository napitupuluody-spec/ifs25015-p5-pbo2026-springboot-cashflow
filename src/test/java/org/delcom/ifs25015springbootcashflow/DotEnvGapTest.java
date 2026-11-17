package org.delcom.ifs25015springbootcashflow;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DotEnvGapTest {

    @AfterEach
    void cleanup() {
        for (String k : new String[]{"DOTENV_E", "DOTENV_F", "DOTENV_G"}) {
            System.clearProperty(k);
        }
    }

    @Test
    void doesNotOverrideEnvironmentVariable(@TempDir Path dir) throws Exception {
        Path f = dir.resolve(".env");
        Files.writeString(f, "PATH=ditimpa\n");
        DotEnv.load(f);
        assertNull(System.getProperty("PATH"));
    }

    @Test
    void handlesShortAndHalfQuotedValues(@TempDir Path dir) throws Exception {
        Path f = dir.resolve(".env");
        Files.writeString(f, "DOTENV_E=\nDOTENV_F='x\nDOTENV_G=z\n");
        DotEnv.load(f);
        assertEquals("", System.getProperty("DOTENV_E"));
        assertEquals("'x", System.getProperty("DOTENV_F"));
        assertEquals("z", System.getProperty("DOTENV_G"));
    }

    @Test
    void unreadableFileThrowsIllegalState(@TempDir Path dir) throws Exception {
        Path f = dir.resolve(".env");
        Files.write(f, new byte[]{(byte) 0xC3, (byte) 0x28}); // UTF-8 tidak valid
        assertThrows(IllegalStateException.class, () -> DotEnv.load(f));
    }
}
