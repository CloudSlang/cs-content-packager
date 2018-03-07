/*
 * (c) Copyright 2018 Micro Focus, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cloudslang.tools.services;

import io.cloudslang.tools.entities.CloudSlangFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.cloudslang.tools.services.CSFileService.getFilesFromDirectory;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang3.StringUtils.replace;

@Slf4j
public class CSDescriptionService {

    public static final String DESCRIPTION = "description";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String RESULT = "result";

    public static void saveDescriptionAsProperties(@NotNull final Path parentDir, @NotNull final Path propertiesFile) {
        final LinkedHashMap<String, String> cpProperties = getCpProperties(parentDir);

        try {
            if (!propertiesFile.toFile().exists()) {
                FileUtils.forceMkdirParent(propertiesFile.toFile());
                FileUtils.touch(propertiesFile.toFile());
            }

            final String propertyFilesContent = cpProperties.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(System.lineSeparator()));

            writeStringToFile(propertiesFile.toFile(), propertyFilesContent, UTF_8, false);
        } catch (IOException e) {
            log.error(format("Could not write properties to file [%s].", propertiesFile));
        }
    }

    public static LinkedHashMap<String, String> getCpProperties(@NotNull final Path parentDir) {
        return getFilesFromDirectory(parentDir).parallelStream()
                .map(CSFileService::readFromFile)
                .map(CSDescriptionService::getProperties)
                .flatMap(properties -> properties.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> {
                    throw new IllegalStateException();
                }, LinkedHashMap::new));
    }

    private static LinkedHashMap<String, String> getProperties(@NotNull final CloudSlangFile cloudSlangFile) {
        log.info(format("Extracting metadata for file: [%s]", cloudSlangFile.getSlangSource().getFilePath()));

        final LinkedHashMap<String, String> propertyMap = new LinkedHashMap<>();
        final String fullSlName = cloudSlangFile.getFullName();
        final String opDescription = cloudSlangFile.getMetadata().getDescription();
        propertyMap.put(format("%s.%s", fullSlName, DESCRIPTION), unixify(opDescription));

        cloudSlangFile.getMetadata().getInputs().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> propertyMap.put(format("%s.%s.%s.%s", fullSlName, INPUT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        cloudSlangFile.getMetadata().getOutputs().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> propertyMap.put(format("%s.%s.%s.%s", fullSlName, OUTPUT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        cloudSlangFile.getMetadata().getResults().entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), unixify(entry.getValue())))
                .forEach(pair -> propertyMap.put(format("%s.%s.%s.%s", fullSlName, RESULT, pair.getLeft(),
                        DESCRIPTION), pair.getRight()));

        return propertyMap;
    }

    private static String unixify(final String unixString) {
        return escapeJava(replace(unixString, System.lineSeparator(), "\n"));
    }
}
