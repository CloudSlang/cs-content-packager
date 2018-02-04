package io.cloudslang.tools.services;

import io.cloudslang.tools.entities.CloudSlangFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.cloudslang.tools.services.CSFileService.getFilesFromDirectory;
import static java.lang.String.format;

@Slf4j
public class CSDescriptionService {

    public static final String DESCRIPTION = "description";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String RESULT = "result";

    public static void saveDescriptionAsProperties(@NotNull final Path parentDir, @NotNull final Path propertiesFile) {
        final Properties cpProperties = getCpProperties(parentDir);

        try {
            if (!propertiesFile.toFile().exists()) {
                FileUtils.forceMkdirParent(propertiesFile.toFile());
                FileUtils.touch(propertiesFile.toFile());
            }

            cpProperties.store(new FileWriter(propertiesFile.toFile()), null);
        } catch (IOException e) {
            log.error(format("Could not write properties to file [%s].", propertiesFile));
        }
    }

    public static Properties getCpProperties(@NotNull final Path parentDir) {
        final Map<Object, Object> descriptions = getFilesFromDirectory(parentDir).parallelStream()
                .map(CSFileService::readFromFile)
                .map(CSDescriptionService::getProperties)
                .flatMap(properties -> properties.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Properties cpProperties = new Properties();
        cpProperties.putAll(descriptions);
        return cpProperties;
    }

    private static Properties getProperties(@NotNull final CloudSlangFile cloudSlangFile) {
        final Properties properties = new Properties();

        final String fullSlName = cloudSlangFile.getFullName();

        final String opDescription = cloudSlangFile.getMetadata().getDescription();
        properties.setProperty(format("%s.%s", fullSlName, DESCRIPTION), unixify(opDescription));

        cloudSlangFile.getMetadata().getInputs().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> properties.setProperty(format("%s.%s.%s.%s", fullSlName, INPUT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        cloudSlangFile.getMetadata().getOutputs().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> properties.setProperty(format("%s.%s.%s.%s", fullSlName, OUTPUT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        cloudSlangFile.getMetadata().getResults().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> properties.setProperty(format("%s.%s.%s.%s", fullSlName, RESULT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        return properties;
    }

    private static String unixify(String string) {
        return StringUtils.replace(string, "\r\n", "\n");
    }
}
