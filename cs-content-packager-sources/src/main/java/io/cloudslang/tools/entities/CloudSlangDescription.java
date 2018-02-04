package io.cloudslang.tools.entities;

public class CloudSlangDescription {
    private final String namespace;
    private final String description;
    private final String input;
    private final String output;
    private final String result;

    public CloudSlangDescription(String namespace, String description, String input, String output, String result) {
        this.namespace = namespace;
        this.description = description;
        this.input = input;
        this.output = output;
        this.result = result;
    }

    public String getNamespace() {return namespace;}
    public String getDescription() {return description;}
    public String getInput() {return input;}
    public String getOutput() {return output;}
    public String getResult() {return result;}
}
