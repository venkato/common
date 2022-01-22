package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide

import groovy.transform.CompileStatic
import net.sf.jremoterun.FindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface  IdePluginsClassloaderInfo extends FindParentClassLoader{


    Collection<String> getPlugins()  throws Exception

}
