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

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MavenUtils {

    //    used to download the dependencies
    public static final String PACKAGE = "package";
    //    used to clean after ourselves
    public static final String CLEAN = "clean";
    //    rock the threads ;)
    public static final String THREAD_COUNT = "2.0C";

    public static final String GROUP_ID = "groupId";
    public static final String ARTIFACT_ID = "artifactId";
    public static final String VERSION = "version";

    public static void runMavenOnPom(@NotNull final Path pom) {
        final InvocationRequest request = getDownloadRequest(pom);

        invokeMavenRequest(request);
    }

    public static void runMavenCleanOnPom(@NotNull final Path pom) {
        final InvocationRequest request = getCleanRequest(pom);

        invokeMavenRequest(request);
    }

    private static void invokeMavenRequest(@NotNull final InvocationRequest request) {
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

    private static InvocationRequest getDownloadRequest(@NotNull final Path pom) {
        return getRequest(pom, Arrays.asList(CLEAN, PACKAGE));
    }

    private static InvocationRequest getCleanRequest(@NotNull final Path pom) {
        return getRequest(pom, ImmutableList.of(CLEAN));
    }

    private static InvocationRequest getRequest(@NotNull final Path pom, @NotNull final List<String> goals) {
        return new DefaultInvocationRequest()
                .setPomFile(pom.toFile())
                .setGoals(goals)
                .setUpdateSnapshots(true)
                .setThreads(THREAD_COUNT);
    }

    @NotNull
    public static Map<String, String> getArtifactMap(@NotNull final String[] splitGav) {
        if (splitGav.length < 3) {
            throw new IllegalArgumentException();
        }

        return new HashMap<String, String>() {{
            put(GROUP_ID, splitGav[0]);
            put(ARTIFACT_ID, splitGav[1]);
            put(VERSION, splitGav[2]);
        }};
    }
}
