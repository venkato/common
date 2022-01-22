package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithAndEndsChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithChildPattern;

import java.util.logging.Logger;

@CompileStatic
class RandomFileLayout {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ChildChildPattern svnEclipseAdaptor = new ExactChildPattern('.metadata/.plugins/org.eclipse.pde.core/.external_libraries').childP(new StartWithChildPattern('org.tigris.subversion.clientadapter_')).childL('lib').childP(new StartWithAndEndsChildPattern('adapter-base-','.jar'));

    @Deprecated
    public static ChildChildPattern svnEclipseAdaptor2 = EclipseFiles.plugins.ref.childP(new StartWithAndEndsChildPattern('org.tigris.subversion.clientadapter_','.jar'));

    public static ChildChildPattern mavenHttpWagon= new ExactChildPattern('lib').childP(new StartWithAndEndsChildPattern('wagon-http-','-shaded.jar'));
    public static ChildChildPattern mavenPlexus= new ExactChildPattern('lib').childP(new StartWithAndEndsChildPattern('plexus-component-annotations-','.jar'));



}
