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

package io.cloudslang.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.cloudslang.tools.services.CSDependenciesService.downloadGavDependencies;
import static io.cloudslang.tools.services.CSDescriptionService.saveDescriptionAsProperties;

public class MainGenerator {

    public static final Path contentFiles = Paths.get(String.valueOf(Paths.get(System.getProperty("user.home") + "\\cloudslang\\cs-content\\content\\io\\cloudslang\\base")));
    public static final Path descriptionPath = Paths.get(System.getProperty("user.home") + "\\Desktop\\cp.properties");
    public static final Path destination = Paths.get(System.getProperty("user.home") + "\\Desktop\\potatoes");

    public static void main(String[] args) {
        saveDescriptionAsProperties(contentFiles, descriptionPath);
        downloadGavDependencies(contentFiles, destination);
    }

}
