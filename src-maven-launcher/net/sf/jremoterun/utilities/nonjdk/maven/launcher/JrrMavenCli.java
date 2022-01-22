package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.maven.DefaultMaven;
import org.apache.maven.cli.CliRequest;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.eclipse.sisu.plexus.PlexusBeanLocator;

import java.util.logging.Logger;

public class JrrMavenCli extends org.apache.maven.cli.MavenCliPublic {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DefaultPlexusContainer container1;
    public DefaultMavenExecutionRequestJrr mavenExecutionRequestJrr = new DefaultMavenExecutionRequestJrr(this);

    public ExecutionListenerMavenProxy executionListenerProxy = new ExecutionListenerMavenProxy();

    public PlexusBeanLocatorProxy plexusBeanLocatorProxy = new PlexusBeanLocatorProxy();

    public CliRequestJrr cliRequestPublic;

    public JrrMavenCli() {
    }

    public JrrMavenCli(ClassWorld classWorld) {
        super(classWorld);
    }

    @Override
    public int doMain(CliRequest cliRequest) {
        return super.doMain(cliRequest);
    }

    @Override
    public void toolchains(CliRequest cliRequest) throws Exception {
        super.toolchains(cliRequest);
        if (JrrMavenCliWrapper.afterToolChainSet != null) {
            JrrMavenCliWrapper.afterToolChainSet.run();
        }
    }


    public DefaultMaven fetchMavenDefault() throws NoSuchFieldException, IllegalAccessException {
        return (DefaultMaven) JrrClassUtils.getFieldValue(this, "maven");
    }

    @Override
    protected void customizeContainer(PlexusContainer container) {
        container1 = (DefaultPlexusContainer) container;
        try {
            PlexusBeanLocator plexusBeanLocator3 = (PlexusBeanLocator) JrrClassUtils.getFieldValue(container1, "plexusBeanLocator");
            if(plexusBeanLocatorProxy.nestedPlexusBeanLocator!=null){
                throw new RuntimeException("nested not nuill");
            }
            plexusBeanLocatorProxy.nestedPlexusBeanLocator = plexusBeanLocator3;
            JrrClassUtils.setFieldValue(container1, "plexusBeanLocator", plexusBeanLocatorProxy);
            super.customizeContainer(container);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
