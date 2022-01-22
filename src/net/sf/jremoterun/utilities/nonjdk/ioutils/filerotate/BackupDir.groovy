package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import org.apache.commons.codec.digest.DigestUtils;

import java.util.logging.Logger;

@CompileStatic
class BackupDir {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseBackupDir
    public int maxDepthDefault



    void rotateDefault(File f ){
        rotate(f,maxDepthDefault)
    }

    void rotate(File f , int maxDepth){
        File toDir = calcToDir(f)
        FileRotate.rotateFile(f,toDir,maxDepth)
    }


    File  calcToDir(File f){
        String pathUnix = f.getAbsoluteFile().getCanonicalFile().getAbsolutePathUnix()
        String aa =new String (DigestUtils.sha256(pathUnix));
        File uu = new File(baseBackupDir,aa)
        assert baseBackupDir.isChildFile(uu)
        uu.mkdir()
        assert uu.exists()
        File infotxt = new File(uu, 'info.txt')
        if(infotxt.exists()){
            assert infotxt.text == pathUnix
        }else{
            infotxt.text = pathUnix
        }
        return uu
    }





}
