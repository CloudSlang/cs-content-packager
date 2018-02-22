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


import io.cloudslang.dependency.api.services.DependencyService;
import io.cloudslang.dependency.impl.services.DependencyServiceImpl;
import io.cloudslang.dependency.impl.services.MavenConfigImpl;
import io.cloudslang.score.events.EventBusImpl;
import io.cloudslang.tools.entities.CloudSlangFile;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CSDependenciesService {
    public static void downloadGavDependencies(@NotNull Path slangDirectory, @NotNull Path destination) {
        final Set<String> gavSet = CSFileService.getFilesFromDirectory(slangDirectory).parallelStream()
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
                .collect(Collectors.toSet());

        final DependencyService dependencyService = getDependencyService();

        final Set<String> dependencies = dependencyService.getDependencies(gavSet);

        dependencies.forEach(System.out::println);
    }

    private static DependencyService getDependencyService() {
        final DependencyServiceImpl dependencyService = new DependencyServiceImpl();

        try {
            final EventBusImpl eventBus = new EventBusImpl();
            final Field eventBusField = DependencyServiceImpl.class.getDeclaredField("eventBus");
            eventBusField.setAccessible(true);
            eventBusField.set(dependencyService, eventBus);

            final MavenConfigImpl mavenConfig = new MavenConfigImpl();
            final Field mavenConfigField = DependencyServiceImpl.class.getDeclaredField("mavenConfig");
            mavenConfigField.setAccessible(true);
            mavenConfigField.set(dependencyService, mavenConfig);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize the DependencyService", e);
        }

        return dependencyService;
    }
}
