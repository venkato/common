package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
class FindCommand extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String dir2
    String type1

    FindCommand() {
        super('find', [])
    }

    void dumpDirs(File f, File dumpTo,int rotateDepth){
        runDir = f
        dir2='.'
        type1='d'
        if(dumpTo!=null){
            process.addWriteOutToFile(dumpTo,rotateDepth)
        }
    }


    String normalizeFile(File name1){
        return normalizeFileS(name1)
    }

    static String normalizeFileS(File name1){
        return name1.getAbsolutePathUnix().replace(':','').replace('/','_')
    }


    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        if(dir2==null){
            throw new NullPointerException("dir is null")
        }
        fullCmd.add(dir2)
        if (type1!=null) {
            fullCmd.add('-type')
            fullCmd.add(type1)
        }
    }
}
