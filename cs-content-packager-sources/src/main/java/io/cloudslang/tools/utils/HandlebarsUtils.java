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
package io.cloudslang.tools.utils;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.cloudslang.tools.services.CSDependenciesService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class HandlebarsUtils {

    public static Optional<Template> loadTemplate(@NotNull final String pathToTemplate) {
        final TemplateLoader templateLoader = new FileTemplateLoader(EMPTY, EMPTY);
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
