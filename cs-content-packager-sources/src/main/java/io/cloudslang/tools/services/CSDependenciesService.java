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


import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.cloudslang.tools.entities.CloudSlangFile;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
                .map(splitGav -> getArtifactMap(splitGav))
                .collect(Collectors.toSet());

        final Optional<Template> optionalTemplate = loadTemplate(TEMPLATE_POM_HBS);

        final Optional<Path> pomPath = optionalTemplate.flatMap(template ->
                getPomContents(template, artifacts, destination.toString()))
                .flatMap(content -> saveToTemp(content));

        pomPath.ifPresent(pom -> runMavenOnPom(pom));

    }

    private static void runMavenOnPom(@NotNull final Path pom) {
        final InvocationRequest request = getRequest(pom);

        try {
            final InvocationResult result = new DefaultInvoker().execute(request);

            if (0 != result.getExitCode()) {
                if (null != result.getExecutionException()) {
                    log.error(result.getExecutionException().toString());
                }
            }
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private static InvocationRequest getRequest(@NotNull final Path pom) {
        final InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pom.toFile());
        request.setGoals(Collections.singletonList("package"));
        request.setUpdateSnapshots(true);
        request.setThreads("2.0C");
        return request;
    }

    @NotNull
    private static Optional<Path> saveToTemp(String content) {
        try {
            final Path path = Files.createTempFile("pom", ".xml");
            FileUtils.forceDeleteOnExit(path.toFile());
            writeStringToFile(path.toFile(), content, UTF_8);
            return Optional.of(path);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    @NotNull
    private static Map<String, String> getArtifactMap(@NotNull final String[] splitGav) {
        if (splitGav.length < 3) {
            throw new IllegalArgumentException();
        }

        return new HashMap<String, String>() {{
            put("groupId", splitGav[0]);
            put("artifactId", splitGav[1]);
            put("version", splitGav[2]);
        }};
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

    private static Optional<Template> loadTemplate(@NotNull final String pathToTemplate) {
        final TemplateLoader templateLoader = new FileTemplateLoader("", "");
        final Handlebars handlebars = new Handlebars(templateLoader).with(EscapingStrategy.XML);
        try {
            return Optional.of(handlebars.compileInline(loadTemplateString(pathToTemplate)));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    @SneakyThrows
    private static String loadTemplateString(@NotNull final String pathToTemplate) {
        final InputStream inputStream = CSDependenciesService.class.getResourceAsStream(pathToTemplate);
        return IOUtils.toString(inputStream, UTF_8);
    }

}
