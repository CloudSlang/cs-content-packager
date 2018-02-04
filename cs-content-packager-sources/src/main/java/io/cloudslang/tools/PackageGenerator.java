package io.cloudslang.tools;

import com.google.common.base.Charsets;
import io.cloudslang.tools.entities.CloudSlangFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FilenameUtils.getExtension;


public class PackageGenerator {

    public static final String OPERATION_JAVA_ACTION_GAV = "operation.java_action.gav";
    public static final String DELIMITER = ".";
    public static final String SL_EXTENSION = "sl";


    public static List<Path> getFilesFromDirectory(final Path contentFiles) {
        try {
            return Files.walk(contentFiles).parallel()
                    .filter(Files::isRegularFile)
                    .filter(path -> getExtension(path.toString()).equals(SL_EXTENSION))
                    .collect(toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static Optional<CloudSlangFile> readFromFile(final Path contentFile) {
        try {
            final String fileContent = readFileToString(contentFile.toFile(), Charsets.UTF_8);
            return Optional.of(new CloudSlangFile(contentFile, fileContent));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
