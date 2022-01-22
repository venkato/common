package com.google.cloud.tools.jib.builder.steps;

import com.google.cloud.tools.jib.configuration.BuildContext;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@CompileStatic
public class StepsRunnerJrr extends StepsRunner{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public StepsRunnerJrr(ExecutorService executorService, BuildContext buildContext) {
        super(MoreExecutors.listeningDecorator(executorService), buildContext);
    }

    @Override
    public BuildResult run() throws ExecutionException, InterruptedException {
        return super.run();
    }
}
