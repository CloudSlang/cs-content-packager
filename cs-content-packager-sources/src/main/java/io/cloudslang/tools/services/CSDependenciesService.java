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


import com.github.jknack.handlebars.Template;
import io.cloudslang.tools.entities.CloudSlangFile;
import io.cloudslang.tools.utils.HandlebarsUtils;
import io.cloudslang.tools.utils.MavenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.cloudslang.tools.services.CSFileService.getFilesFromDirectory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.splitPreserveAllTokens;

@Slf4j
public class CSDependenciesService {

    public static final String TEMPLATE_POM_HBS = "/templates/pom.hbs";

    public static void downloadGavDependencies(@NotNull final Path slangDirectory, @NotNull final Path destination) {
        final Set<Map<String, String>> artifacts = getFilesFromDirectory(slangDirectory).parallelStream()
                .map(CSFileService::readFromFile)
                .filter(CloudSlangFile::isJavaOperation)
                .flatMap(slangFile -> {
                    try {
                        return Stream.of(slangFile.getGav());
                    } catch (Throwable t) {
                        log.warn(String.format("Failed to get GAV for file %s.", slangFile.getFullName()));
                        return Stream.empty();
                    }
                })
                .map(gav -> splitPreserveAllTokens(gav, ":"))
                .map(MavenUtils::getArtifactMap)
                .collect(Collectors.toSet());

        final Optional<Template> optionalTemplate = HandlebarsUtils.loadTemplate(TEMPLATE_POM_HBS);

        final Optional<Path> pomPath = optionalTemplate.flatMap(template ->
                getPomContents(template, artifacts, destination.toString()))
                .flatMap(CSDependenciesService::saveToTemp);

        pomPath.ifPresent(MavenUtils::runMavenOnPom);

        pomPath.ifPresent(MavenUtils::runMavenCleanOnPom);
    }

    @NotNull
    private static Optional<Path> saveToTemp(@NotNull final String pomContent) {
        try {
            final Path dirPath = Files.createTempDirectory("cs_temp_");
            FileUtils.forceDeleteOnExit(dirPath.toFile());

            final Path pomFile = Files.createTempFile(dirPath, "pom_", ".xml");
            FileUtils.forceDeleteOnExit(pomFile.toFile());

            writeStringToFile(pomFile.toFile(), pomContent, UTF_8);
            return Optional.of(pomFile);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    private static Optional<String> getPomContents(@NotNull final Template template,
                                                   @NotNull final Set<Map<String, String>> artifacts,
                                                   @NotNull final String destinationDirectory) {

        final Map<String, Object> toComplete = new HashMap<>();
        toComplete.put("dependencies", artifacts);
        toComplete.put("outputDirectory", destinationDirectory);

        try (final StringWriter stringWriter = new StringWriter()) {
            template.apply(toComplete, stringWriter);
            stringWriter.flush();
            return Optional.of(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
