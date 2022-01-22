package com.google.cloud.tools.jib.api;

import com.google.cloud.tools.jib.builder.TimerEventDispatcher;
import com.google.cloud.tools.jib.builder.steps.BuildResult;
import com.google.cloud.tools.jib.configuration.BuildContext;
import com.google.cloud.tools.jib.configuration.ImageConfiguration;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@CompileStatic
public class JibContainerBuilderOriginJrr extends JibContainerBuilder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public JibContainerBuilderOriginJrr(RegistryImage baseImage) {
        super(baseImage);
    }

    public JibContainerBuilderOriginJrr(DockerDaemonImage baseImage) {
        super(baseImage);
    }

    public JibContainerBuilderOriginJrr(TarImage baseImage) {
        super(baseImage);
    }

    public JibContainerBuilderOriginJrr(ImageConfiguration imageConfiguration, BuildContext.Builder buildContextBuilder) {
        super(imageConfiguration, buildContextBuilder);
    }

    @Override
    public JibContainer containerize(Containerizer containerizer) throws InterruptedException, RegistryException, IOException, CacheDirectoryCreationException, ExecutionException {
        return super.containerize(containerizer);
    }

    public JibContainer containerize2(Containerizer containerizer) throws Exception {
        BuildContext buildContext1 = toBuildContext(containerizer);
        return containerize3(containerizer, buildContext1, this);
    }

    public JibContainer containerize3(Containerizer containerizer, BuildContext buildContext1, JibContainerBuilderOriginJrr containerBuilder)
            throws Exception {
        TimerEventDispatcher ignored = new TimerEventDispatcher(buildContext1.getEventHandlers(), containerizer.getDescription());
        try (BuildContext buildContext = buildContext1) {
            JrrClassUtils.invokeJavaMethod(containerBuilder, "logSources", buildContext.getEventHandlers());
            BuildResult buildResult = containerizer.run(buildContext);
            return JibContainer.from(buildContext, buildResult);
        }
    }

    @Override
    public BuildContext toBuildContext(Containerizer containerizer) throws CacheDirectoryCreationException {
        return super.toBuildContext(containerizer);
    }
}
