package net.sf.jremoterun.utilities.nonjdk.docker

import com.google.cloud.tools.jib.api.LayerConfiguration
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.buildplan.FileEntriesLayer
import com.google.cloud.tools.jib.api.buildplan.FilePermissions
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.file.Path
import java.nio.file.Paths;
import java.util.logging.Logger;

@CompileStatic
class DockerLayerJrr {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    FileEntriesLayer.Builder layer1  = FileEntriesLayer.builder();
//    LayerConfiguration.Builder layer2 =LayerConfiguration.builder();

    public FilePermissions defaultPermissions = FilePermissions.fromOctalString('744')

    public List<String> ignoreDirs = []

    void addAllFilesInPath(File dir,AbsoluteUnixPath path){
        assert dir.isDirectory()
        dir.listFiles().toList().each {
            File f = it;
            if(f.isFile()){
                addFile(f,path.resolve(f.getName()))
            }else{
                assert f.isDirectory()
                if(isNeededIncludeDir(f)){
                    AbsoluteUnixPath subpath2 = path.resolve(f.getName())
                    addAllFilesInPath(f,subpath2)
                }
            }

        }
    }

    boolean isNeededIncludeDir(File dir){
        if(ignoreDirs.contains(dir.getName())){
            return false
        }
        return true
    }


    void addFileOrFolder(DockerFileJrr df){
        if(df.file.isDirectory()){
            addAllFilesInPath(df.file, df.unixPath)
        }else{
            addFile(df.file,df.unixPath)
        }
    }


    void addFile(File f , AbsoluteUnixPath fullPath){
        assert f.isFile()
        Path path1 = Paths.get(f.getAbsolutePath());
        layer1.addEntry(path1,fullPath,defaultPermissions)
    }

    void addEmptyDir(AbsoluteUnixPath dir){
        layer1.addEntry(Paths.get(new File('c:/').getAbsolutePath()),dir)
    }

    void addEmptyDir(String dir){
          addEmptyDir(AbsoluteUnixPath.get(dir))
    }


}
