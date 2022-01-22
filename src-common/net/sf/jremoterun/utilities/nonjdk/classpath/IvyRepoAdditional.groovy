package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.finders.GrapeFileFinder;

import java.util.logging.Logger;

@CompileStatic
class IvyRepoAdditional extends GrapeFileFinder{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GrapeFileFinder mainRepo;
    public GrapeFileFinder auxRepo;

    IvyRepoAdditional(File mavenLocalDirAux,GrapeFileFinder mainRepo) {
        super(mainRepo.getMavenLocalDir2())
        this.mainRepo = mainRepo
        auxRepo = new GrapeFileFinder(mavenLocalDirAux)
    }

    static void prependRepo(File mavenLocalDir2){
        IvyRepoAdditional add2 = new IvyRepoAdditional(mavenLocalDir2, MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder)
        MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder = add2;
    }

    @Override
    File findArtifact(MavenId mavenId, String fileType) {
        File f = auxRepo.findArtifact(mavenId, fileType)
        if(f!=null && f.exists()){
            return f
        }
        return mainRepo.findArtifact(mavenId,fileType)
    }

    @Override
    File getMavenLocalDir2() {
        return mainRepo.getMavenLocalDir2()
    }

    @Override
    void setMavenLocalDir2(File mavenLocalDir2) {
        mainRepo.setMavenLocalDir2(mavenLocalDir2)
    }

    @Override
    MavenId detectMavenIdFromFileName(File file, boolean logMismatch, String fileType) {
        MavenId m = auxRepo.detectMavenIdFromFileName(file, logMismatch, fileType)
        if(m!=null){
            return m
        }
        return mainRepo.detectMavenIdFromFileName(file, logMismatch, fileType)
    }


    @Override
    List<String> findAllVersions(MavenId mavenId, String fileType) {
        List<String> s = auxRepo.findAllVersions(mavenId, fileType)
        s.addAll(mainRepo.findAllVersions(mavenId, fileType))
        return s
    }
}
