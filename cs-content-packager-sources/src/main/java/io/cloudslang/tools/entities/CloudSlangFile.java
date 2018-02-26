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

    public boolean isJavaOperation() {
        final YamlReader yamlReader = new YamlReader(new StringReader(slangSource.getContent()));
        try {
            final Optional<Object> gavOpt = simpleYPath(yamlReader.read(),
                    "operation.java_action.gav", ".");

            return gavOpt.isPresent();
        } catch (YamlException e) {
            return false;
        }
    }

    public String getGav() {
        final YamlReader yamlReader = new YamlReader(new StringReader(slangSource.getContent()));
        try {
            final Optional<Object> gavOpt = simpleYPath(yamlReader.read(),
                    "operation.java_action.gav", ".");

            final Object gav = gavOpt.orElseGet(() -> {
                throw new RuntimeException("GAV not found in Slang file.");
            });

            return gav.toString();
        } catch (YamlException e) {
            throw new RuntimeException("Failed to parse the Slang file.");
        }
    }
}
