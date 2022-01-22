package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo;

import java.util.logging.Logger;

@CompileStatic
interface MavenIdAndRepoCustomSourceContains extends CustomSourceRefRedirect{


    MavenIdAndRepo getMavenIdAndRepo();




}
