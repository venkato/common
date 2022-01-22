package net.sf.jremoterun.utilities.nonjdk.timmoson

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import timmoson.client.getmanyfiles.GetFileClient
import timmoson.server.service.GetFileService

import java.text.DecimalFormat
import java.util.logging.Logger;

@CompileStatic
class RemoteFileResolver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GetFileClient getFileClient;

    File baseDir;

    File tmpDir;

    int tmpIndex = 1

    public static DecimalFormat decimalFormat1 = new DecimalFormat('000')

    RemoteFileResolver(GetFileClient getFileClient, File baseDir) {
        this.getFileClient = getFileClient
        this.baseDir = baseDir
        assert baseDir.exists()
        tmpDir=new File(baseDir,'tmpdir')
        if(tmpDir.exists()){
            tmpDir.listFiles().toList().each {
                it.delete()
            }
        }
        tmpDir.mkdir()
        assert tmpDir.exists()
    }

    File createTmpFile(){
        File file1 = new File(tmpDir, decimalFormat1.format(tmpIndex)+'.dat')
        tmpIndex++
        if(file1.exists()){
            return createTmpFile()
        }
        return file1;
    }

    File getDir(RemoteRef ref) {
        File tmpfile = createTmpFile()
        File localDir = resolve1(ref)
        Map<String, String> sha1 = [:];
        Map<String, Long> lengthes = [:];
        List<File> filesLocal = []
        if (localDir.exists()) {
            localDir.traverse {
                if (it.isFile()) {
                    filesLocal.add(it)
                }
            }
            filesLocal.each {
                File file1 = it
                long length1 = file1.length()
                String childPath = localDir.getPathToParent(file1)
                sha1.put(childPath, GetFileService.calcSha(file1))
                lengthes.put(childPath, length1)
                log.info("${file1} ${length1} lengthes = ${lengthes}")
            }
        }
        localDir.mkdirs()
        assert localDir.exists()
        getFileClient.getDirOrFile(ref, sha1, lengthes, localDir, tmpfile)
        tmpfile.delete()
        return localDir;
    }

    List<File> getFiles(List<RemoteRef> fileRefs) {
        File tmpfile = createTmpFile()
        List<TransferObject> transferObjectList = []
        List<File> targetFiles = []
        fileRefs.each {

            RemoteRef ref1 = it
            File file1 = resolve1(ref1)
            TransferObject transferObject = new TransferObject()
            transferObject.remoteRef = ref1
            if (file1.exists()) {
                transferObject.sha1 = GetFileService.calcSha(file1)
                transferObject.fileLength = file1.length()
            }
            transferObjectList.add(transferObject)
            targetFiles.add(file1)
            //   it.child.approximatedName()
        }
        getFileClient.getFileMany(transferObjectList, targetFiles, tmpfile)
        tmpfile.delete()
        return targetFiles
    }


    File resolve1(RemoteRef ref) {
        File file1 = new File(baseDir, resolveBase(ref.baseRef));
        File file2 = new File(file1, resolveChild(ref.child));
        return file2
    }


    String resolveBase(ToFileRef2 toFileRef2) {
        return toFileRef2.toString()
    }

    String resolveChild(ChildPattern child) {
        return child.approximatedName()
    }
}
