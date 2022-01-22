package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.logging.Logger;

@CompileStatic
public class DefaultMavenExecutionRequestJrr extends DefaultMavenExecutionRequest{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrMavenCli mavenCli;

    public DefaultMavenExecutionRequestJrr(JrrMavenCli mavenCli) {
        this.mavenCli = mavenCli;
    }

    public boolean setEl = false;

    @Override
    public MavenExecutionRequest setExecutionListener(ExecutionListener executionListener) {
        if(setEl){
            throw new RuntimeException("el set before");
        }

        setEl = true;
        mavenCli.executionListenerProxy.nestedListeners.add( executionListener);
        return super.setExecutionListener(mavenCli.executionListenerProxy);
    }
}
