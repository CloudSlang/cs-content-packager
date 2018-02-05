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

import io.cloudslang.lang.compiler.MetadataExtractor;
import io.cloudslang.lang.compiler.MetadataExtractorImpl;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.compiler.modeller.MetadataModellerImpl;
import io.cloudslang.lang.compiler.modeller.model.Metadata;
import io.cloudslang.lang.compiler.parser.MetadataParser;
import io.cloudslang.lang.compiler.parser.utils.MetadataValidatorImpl;
import io.cloudslang.lang.compiler.parser.utils.ParserExceptionHandler;
import io.cloudslang.tools.entities.CloudSlangFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class CSFileService {
    public static final String SL_EXTENSION = "sl";

    public static List<Path> getFilesFromDirectory(final Path contentFiles) {
        try {
            return Files.walk(contentFiles)
                    .filter(Files::isRegularFile)
                    .filter(path -> getExtension(path.toString()).equals(SL_EXTENSION))
                    .collect(toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static CloudSlangFile readFromFile(final Path contentFile) {
        final SlangSource slangSource = SlangSource.fromFile(contentFile.toFile());
        final Metadata metadata = getMetadataExtractor().extractMetadata(slangSource);
        return new CloudSlangFile(slangSource, metadata);
    }

    private static MetadataExtractor getMetadataExtractor() {
        final MetadataExtractorImpl metadataExtractor = new MetadataExtractorImpl();
        metadataExtractor.setMetadataModeller(new MetadataModellerImpl());

        final MetadataParser metadataParser = new MetadataParser();
        metadataParser.setParserExceptionHandler(new ParserExceptionHandler());
        metadataExtractor.setMetadataParser(metadataParser);

        final MetadataValidatorImpl metadataValidator = new MetadataValidatorImpl();
        metadataValidator.setMetadataParser(metadataParser);
        metadataExtractor.setMetadataValidator(metadataValidator);
        return metadataExtractor;
    }
}
