package io.cloudslang.tools;

import io.cloudslang.tools.services.CSDescriptionService;
import org.apache.maven.plugins.dependency.fromDependencies.CopyDependenciesMojo;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.cloudslang.tools.services.CSDescriptionService.saveDescriptionAsProperties;

public class MainGenerator {

    public static final Path contentFiles = Paths.get(String.valueOf(Paths.get(System.getProperty("user.home") + "\\cloudslang\\cloud-slang-content\\content\\io\\cloudslang\\base")));
    public static final Path descriptionPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato\\cp.properties");
    public static final Path dependenciesPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\potato\\repo");


    public static void main(String[] args) throws Exception {
        saveDescriptionAsProperties(contentFiles, descriptionPath);
    }

}
