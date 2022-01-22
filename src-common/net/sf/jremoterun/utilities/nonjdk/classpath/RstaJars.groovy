package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

//import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences

import java.util.logging.Logger

@CompileStatic
class RstaJars {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean useCustom = false;

    static ToFileRef2 rsta(){
//        if(useCustom) {
//            return GitReferences.rsta;
//        }
        return new MavenId("com.fifesoft:languagesupport:3.3.0")
    }

    static MavenId rstaAutoCompetion(){
//        if(useCustom) {
//            return GitReferences.rstaAutoCompetion
//        }
        return new MavenId("com.fifesoft:autocomplete:3.3.1")
    }

    static MavenId rstaui(){
        if(useCustom) {
            return LatestMavenIds.rstaui.m
        }
        return new MavenId("com.fifesoft:rstaui:3.3.1")
    }

    static MavenId fifeRtext(){
        if(useCustom) {
            return LatestMavenIds.fifeRtext.m
        }
        return new MavenId("com.fifesoft.rtext:fife.common:6.0.3")
    }

    static MavenId rsyntaxtextarea(){
        if(useCustom) {
            return LatestMavenIds.rsyntaxtextarea.m;
        }
        return new MavenId("com.fifesoft:rsyntaxtextarea:3.4.0")
    }




}
