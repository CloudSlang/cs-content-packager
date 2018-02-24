package io.cloudslang.tools.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
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
        request.setGoals(Arrays.asList(PACKAGE, CLEAN));
        request.setUpdateSnapshots(true);
        request.setThreads(THREAD_COUNT);
        return request;
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
