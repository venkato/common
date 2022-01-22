package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Project;

import java.util.logging.Logger;

@groovy.transform.TupleConstructor
@CompileStatic
class ProjectFinder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Project defaultProject;
    public Set<Project> allprojects;
    public boolean searchNameByAny = true;


    void init() {
        defaultProject = GradleEnvsUnsafe.fetchDefaultProject();
        // can getAllprojects() method affect performance by configuring all projects?
        allprojects = defaultProject.getAllprojects()
        if (allprojects.size() == 0) {
            throw new Exception("No project found")
        }
    }


    Project findProject(String nameContains) {
        if (allprojects == null) {
            init()
        }
        Set<Project> projects1 = allprojects.findAll { isProjectMatched(it, nameContains) }
        if (projects1.size() == 0) {
            throw new Exception("No project found with subname ${nameContains} of ${listAllProjects()}")
        }
        if (projects1.size() > 1) {
            throw new Exception("${projects1.size()} project found with subname ${listProjects(projects1)}")
        }
        return projects1[0]
    }

    String convertToStringProject(Project project) {
        return project.getDisplayName()
    }

    String listProjects(Collection<Project> prjs) {
        return prjs.collect { convertToStringProject(it) }.sort().join(', ')
    }

    String listAllProjects() {
        return listProjects(allprojects);
    }

    boolean isProjectMatched(Project project, String nameContains) {
        if (convertToStringProject(project).contains(nameContains)) {
            return true
        }
        if (searchNameByAny) {
            if (project.getDisplayName().contains(nameContains)) {
                return true
            }
            if (project.getName().contains(nameContains)) {
                return true
            }
            if (project.getPath().contains(nameContains)) {
                return true
            }
        }

        return false
    }


}
