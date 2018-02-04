package io.cloudslang.tools.entities;

import java.nio.file.Path;

public class CloudSlangFile {
    private final Path filePath;
    private final String content;

    public CloudSlangFile(Path filePath, String content) {
        this.filePath = filePath;
        this.content = content;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getContent() {
        return content;
    }
}
