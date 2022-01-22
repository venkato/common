package org.apache.maven.cli;

import org.codehaus.plexus.classworlds.ClassWorld;

public class MavenCliPublic extends MavenCli{

    public MavenCliPublic() {
    }

    public MavenCliPublic(ClassWorld classWorld) {
        super(classWorld);
    }


    @Override
    public void toolchains(CliRequest cliRequest) throws Exception {
        super.toolchains(cliRequest);
    }

    @Override
    public int doMain(CliRequest cliRequest) {
        return super.doMain(cliRequest);
    }
}
