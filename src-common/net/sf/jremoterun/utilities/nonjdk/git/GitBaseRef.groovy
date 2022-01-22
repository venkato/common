package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI;

import java.util.logging.Logger;

@CompileStatic
interface GitBaseRef extends ChildFileLazy {


    String getBaseRef();

    /**
     * use https://docs.gitlab.com/ee/api/api_resources.html#group-resources to get all child projects
     */
    @Override
    GitSpec childL(String child) ;

}
