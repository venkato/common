package net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenIdContains

@CompileStatic
abstract class  ClassPathMavenIds {

    abstract List<MavenIdContains> getMavenIdsCustom()


    public List<String> loadMavenIds(){
        new ArrayList(getMavenIdsCustom().collect{it.m.toString()})
    }



}
