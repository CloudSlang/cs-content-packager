package io.cloudslang.tools.entities;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.compiler.modeller.model.Metadata;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.util.Optional;

import static io.cloudslang.tools.utils.YamlUtils.simpleYPath;
import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.getBaseName;

@Slf4j
@Value
public class CloudSlangFile {
    SlangSource slangSource;
    Metadata metadata;

    public String getFullName() {
        try {
            final YamlReader yamlReader = new YamlReader(new StringReader(slangSource.getContent()));
            final Optional<Object> namespace = simpleYPath(yamlReader.read(), "namespace", ".");
            if (namespace.isPresent()) {
                return format("%s.%s", namespace.get().toString(), getBaseName(slangSource.getName()));
            }
        } catch (YamlException exception) {
            log.error("Failed to parse SL file.", exception);
        }
        return slangSource.getName();
    }
}
