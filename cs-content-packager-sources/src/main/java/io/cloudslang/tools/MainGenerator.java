package io.cloudslang.tools;

import io.cloudslang.tools.entities.CloudSlangFile;
import io.cloudslang.tools.entities.CloudSlangOperations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.cloudslang.tools.PackageGenerator.getFilesFromDirectory;

public class MainGenerator {

    public static final Path contentFiles = Paths.get(String.valueOf(Paths.get(System.getProperty("user.home") + "\\cloudslang\\cloud-slang-content\\content\\io\\cloudslang\\base")));
    public static final Path descriptionPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato");
    public static final Path dependenciesPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato");


    public static void main(String[] args) throws Exception {
        final List<CloudSlangFile> cloudSlangFiles = getFilesFromDirectory(contentFiles).stream()
                .map(PackageGenerator::readFromFile)
                .flatMap(optionalString -> optionalString.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());

        System.out.println(cloudSlangFiles);
    }

}
