package org.delcom.ifs25015springbootcashflow.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.delcom.ifs25015springbootcashflow.adapter.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileStorageGapTest {

    @Test
    void storeFailsWhenUploadFolderIsGone(@TempDir Path dir) throws Exception {
        FileStorage storage = new FileStorage(dir.resolve("up").toString());
        Files.delete(storage.root());
        assertThrows(UncheckedIOException.class, () -> storage.store(new byte[]{1}, "a.png"));
    }

    @Test
    void deleteFailsOnNonEmptyDirectory(@TempDir Path dir) throws Exception {
        FileStorage storage = new FileStorage(dir.resolve("up").toString());
        Path sub = storage.root().resolve("sub");
        Files.createDirectories(sub);
        Files.writeString(sub.resolve("f.txt"), "x");
        assertThrows(UncheckedIOException.class, () -> storage.delete("sub"));
    }
}
