package net.sf.jremoterun.utilities.nonjdk.git.foldersync

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.git.CommitIdCanonicalTreeParser
import net.sf.jremoterun.utilities.nonjdk.git.DiffApplierFast
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.logging.Logger;

@CompileStatic
class FolderSync {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File dirFrom;
    public File dirTo;
    List<File> copiedFiles = []

    FolderSync(File dirFrom, File dirTo) {
        this.dirFrom = dirFrom
        this.dirTo = dirTo
        assert dirFrom.exists()
        assert dirTo.exists()
    }

    void doJob(){
        syncFiles()
        syncGit()
    }

    void syncFiles(){
        syncFilesImpl(dirFrom,dirTo);
    }

    void syncFilesImpl(File from, File to){
        List<File> list1 = from.listFiles().toList()
        list1.each {
            File dest = new File(to,it.getName())
            if(it.isDirectory())            {
                syncFilesImpl(it,dest);
            }else {
                assert it.isFile()
                boolean isNeedSync2 = isNeedSync(it)
                if(isNeedSync2){
                    FileUtilsJrr.copyFile(it,dest)
                    copiedFiles.add(dest)
                }
            }
        }
    }

    File findGitDir(File childDir){
        assert childDir.exists()
        File checkDir = new File(childDir,'.git')
        if(checkDir.exists()){
            assert new File(checkDir,'index').exists()
            return childDir
        }
        File parentDir = childDir.getParentFile()
        if(parentDir==null){
            throw new NullPointerException("Failed find parent for ${childDir}")
        }
        return findGitDir(parentDir)
    }

    List<String> addedFile3
    File gitDir;
    String pathToParentTo;
    List<DiffEntry> diffEntries;
    DiffApplierFast applierFast
    GitRepoUtils gitRepoUtils;

    void syncGit(){
        gitDir = findGitDir(dirTo)
        gitRepoUtils = new GitRepoUtils(gitDir)
        pathToParentTo = gitDir.getPathToParent(dirTo)
        addedFile3 = copiedFiles.collect { gitDir.getPathToParent(it) }
        addToIndex3(addedFile3,true,gitRepoUtils)
        addToIndex3(addedFile3,false,gitRepoUtils)
        DirCacheIterator stagingTreeIterator = gitRepoUtils.buildStagingTreeIterator()
        RevCommit headCommit = gitRepoUtils.findCommit('HEAD')
        CommitIdCanonicalTreeParser headTreeIterator = gitRepoUtils.getTreeIterator(headCommit)
        diffEntries = gitRepoUtils.diffStd(stagingTreeIterator, headTreeIterator)
        List<DiffEntry> entries = diffEntries.findAll { considerEntryUpdateFromHead(it) }
        if(entries.size()>0){
            applierFast = new DiffApplierFast(entries, gitRepoUtils)
            applierFast.doStuff()
        }
    }

    boolean considerEntryUpdateFromHead(DiffEntry diffEntry){
        String oldPath = diffEntry.getOldPath()
        String newPath = diffEntry.getNewPath()
        if(oldPath!=null && oldPath!='/dev/null'){
            if(!isNeedSync(oldPath)){
                return false
            }
            if(pathToParentTo.length()>0 && !oldPath.startsWith(pathToParentTo)){
                return false
            }
        }
        if(newPath!=null&& newPath!='/dev/null'){
            if(!isNeedSync(newPath)){
                return false
            }
            if(pathToParentTo.length()>0 &&!newPath.startsWith(pathToParentTo)){
                return false
            }

        }

        if(diffEntry.getChangeType()==DiffEntry.ChangeType.MODIFY){
            if(!addedFile3.contains(oldPath)){
                return true
            }
        }
        if(diffEntry.getChangeType()==DiffEntry.ChangeType.DELETE){
            // means file present in index
            if(!addedFile3.contains(oldPath)){
                return true
            }
        }
        if(diffEntry.getChangeType()==DiffEntry.ChangeType.ADD){
            // means file stay to remove from index
            return isNeedRemoveFromStage(newPath)
        }
        return false
    }

    boolean isNeedRemoveFromStage(String path){
        return isNeedSync(path)
    }


    void addToIndex3(Collection<String> paths, boolean setUpdated1,GitRepoUtils gitRepoUtils) {
        AddCommand addCommand = gitRepoUtils.git.add()
        addCommand.setUpdate(setUpdated1)
        gitRepoUtils.doCustomGitTuning(addCommand)
        paths.each { addCommand.addFilepattern(it) }
        addCommand.call()
    }



    boolean isNeedSync(String path){
        return true
    }

    boolean isNeedSync(File f){
        String pathToParent = dirFrom.getPathToParent(f)
        return isNeedSync(pathToParent)
    }






}
