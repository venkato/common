package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.plugin.management.internal.DefaultPluginManagementSpec;

import java.util.logging.Logger;

@CompileStatic
class GradleRepoUtils implements ProjectEvaluationListener{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean addedMyRepo = false

    public URL url9

    GradleRepoUtils(URL url9) {
        this.url9 = url9
    }

    @Override
    void beforeEvaluate(Project project33) {
        addIfNeeded(project33)
    }

    void addIfNeeded(Project project33){
        if(!addedMyRepo){
            addedMyRepo = true
            addProjectRepo(project33,url9)
        }
    }

    @Override
    void afterEvaluate(Project project, ProjectState state) {
    }

    static void addDefaultRepo(URL url8){
        GradleEnvsUnsafe.gradle.addProjectEvaluationListener(new GradleRepoUtils(url8))
    }

    static void addProjectRepo(Project project,URL url){
        addMavenRepo(getRepositories2(project),url)
    }

    static void addSettingsRepo(URL url){
        GradleEnvsUnsafe.gradle.beforeSettings {
            org.gradle.api.initialization.Settings settings2 = it as Settings;
            addMavenRepo(getPluginRepo2(settings2), url);
        }
    }


    static DefaultRepositoryHandler getRepositories() {
        getRepositories2(GradleEnvsUnsafe.fetchDefaultProject())
    }

    static DefaultRepositoryHandler getRepositories2(org.gradle.api.Project prj) {
        return prj.getRepositories() as DefaultRepositoryHandler
    }


    static BaseRepositoryFactory getBaseRepositoryFactory(DefaultRepositoryHandler repositories1) {
        return JrrClassUtils.getFieldValue(repositories1, 'repositoryFactory') as BaseRepositoryFactory
    }

    static MavenArtifactRepository addMavenRepo(DefaultRepositoryHandler repositories1,URL url) {
        BaseRepositoryFactory rf = getBaseRepositoryFactory(repositories1)
        MavenArtifactRepository mavenRepository = rf.createMavenRepository()
        mavenRepository.setUrl(url.toURI())
        repositories1.addRepository(mavenRepository, 'maven')
        return mavenRepository
    }

    static org.gradle.plugin.management.internal.DefaultPluginManagementSpec getPluginManagement(){
        return GradleEnvsUnsafe.gradle.getSettings().getPluginManagement() as DefaultPluginManagementSpec
    }

    static DefaultRepositoryHandler getPluginRepo2(org.gradle.api.initialization.Settings settings2){
        return settings2.getPluginManagement().getRepositories() as DefaultRepositoryHandler
    }

    static DefaultRepositoryHandler getPluginRepo(){
        return getPluginManagement().getRepositories() as DefaultRepositoryHandler
    }

}
