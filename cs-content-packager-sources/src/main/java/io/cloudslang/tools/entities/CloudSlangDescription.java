package io.cloudslang.tools.entities;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

@Value(staticConstructor = "of")
public class CloudSlangDescription {


    String fullSlName;
    String description;
    Map<String, String> inputs;
    Map<String, String> outputs;
    Map<String, String> results;


}
