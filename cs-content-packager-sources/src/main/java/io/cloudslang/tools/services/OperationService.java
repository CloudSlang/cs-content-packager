package io.cloudslang.tools.services;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

public class OperationService {

    private String files;

    public String CloudSlangOperations(Path pathToContent) {
        this.files = StringUtils.defaultIfEmpty(files, "No path defined");

        return files;
    }


}
