package io.cloudslang.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.cloudslang.tools.services.CSDescriptionService.saveDescriptionAsProperties;

public class MainGenerator {

    public static final Path contentFiles = Paths.get(String.valueOf(Paths.get(System.getProperty("user.home") + "\\cloudslang\\cloud-slang-content\\content\\io\\cloudslang\\base")));
    public static final Path descriptionPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato\\cp.properties");
    public static final Path dependenciesPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato\\repo");


    public static void main(String[] args) {
        saveDescriptionAsProperties(contentFiles, descriptionPath);
    }

}
