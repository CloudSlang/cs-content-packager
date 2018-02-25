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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static io.cloudslang.tools.services.CSDependenciesService.downloadGavDependencies;


@Mojo(name = "copy-dependencies", threadSafe = true, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)

public class CopyDependenciesMojo extends AbstractMojo {
    @Parameter(property = "contentFiles", defaultValue = "${project.build.outputDirectory}/Content/Library")
    protected File contentFiles;

    @Parameter(property = "destination", defaultValue = "${project.build.outputDirectory}/Lib")
    protected File destination;

    public void execute() throws MojoExecutionException {

        try {
            downloadGavDependencies(contentFiles.toPath(), destination.toPath());
        } catch (Exception e) {
            throw new MojoExecutionException("An error occurred while trying to download dependencies for: " + contentFiles.getAbsolutePath(), e);
        }
    }
}
