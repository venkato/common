package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import org.apache.maven.cli.CliRequestPublic;
import org.codehaus.plexus.classworlds.ClassWorld;

public class CliRequestJrr extends CliRequestPublic {

    public CliRequestJrr(String[] args, ClassWorld classWorld) {
        super(args, classWorld);
    }
}
