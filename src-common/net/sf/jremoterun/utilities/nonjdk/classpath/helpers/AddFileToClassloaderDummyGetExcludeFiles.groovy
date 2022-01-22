package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy;

import java.util.List;

@CompileStatic
public class AddFileToClassloaderDummyGetExcludeFiles {


    public AddFileToClassloaderDummy addFileToClassloaderDummy;

    public List<String> excludedFiles = [];
    public HashSet<String> excludedFilesUsed = new HashSet<>();


    public AddFileToClassloaderDummyGetExcludeFiles(AddFileToClassloaderDummy addFileToClassloaderDummy) {
        this.addFileToClassloaderDummy = addFileToClassloaderDummy;
    }

    List<File> getWithoutExcludedFiles(){
        if( addFileToClassloaderDummy.addedFilesWithOrder.size()==0){
            throw new Exception("no files in adder")
        }
//        if(excludedFiles.size()==0){
//            throw new Exception("no excluded files")
//        }
        List<File> files234 = addFileToClassloaderDummy.addedFilesWithOrder.findAll { isNeededFile(it) }
        List<String> listOfStrings = excludedFiles - excludedFilesUsed
        if(listOfStrings.size()!=0){
            throw new Exception("found not used excluded files ${listOfStrings.size()} : ${listOfStrings.sort().join(', ')}")
        }
        return files234
    }

    boolean isNeededFile(File f){
        if(excludedFiles.contains( f.getName())){
            excludedFilesUsed.add(f.getName())
            return false
        }
        return true
    }

}
