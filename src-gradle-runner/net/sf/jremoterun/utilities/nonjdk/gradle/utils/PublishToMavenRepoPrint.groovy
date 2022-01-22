package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;

import java.util.logging.Logger;

@CompileStatic
class PublishToMavenRepoPrint {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void selfAdd(){
        GradleEnvsUnsafe.gradle.projectsLoaded{
            addToAllProjects()
        }
    }

    void addToAllProjects(){
        Project defaultProject = GradleEnvsUnsafe.fetchDefaultProject()
        Set<Project> allprojects = defaultProject.getAllprojects()
        if (allprojects.size() == 0) {
            throw new Exception("No project found")
        }
        allprojects.each {
            addToProject(it)
        }
    }

    void addToProject(Project project1){
        project1.getTasks().withType(PublishToMavenRepository).each {
            PublishToMavenRepository publishToMavenRepository23 = it
            publishToMavenRepository23.doFirst {
                doPrintBefore(publishToMavenRepository23)
            }
            publishToMavenRepository23.doLast {
                doPrintAfter(publishToMavenRepository23)
            }
        }
    }

    void doPrintBefore(PublishToMavenRepository publishToMavenRepository){
        MavenPublication publication = publishToMavenRepository.publication
        log.warn("publishing ${publication.groupId}:${publication.artifactId}:${publication.version} to ${publishToMavenRepository.repository.url} ..")
    }

    void doPrintAfter(PublishToMavenRepository publishToMavenRepository){
        MavenPublication publication = publishToMavenRepository.publication
        log.warn("published ${publication.groupId}:${publication.artifactId}:${publication.version} to ${publishToMavenRepository.repository.url}")
    }


}
