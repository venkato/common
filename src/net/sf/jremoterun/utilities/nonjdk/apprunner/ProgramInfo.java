package net.sf.jremoterun.utilities.nonjdk.apprunner;

import groovy.transform.Canonical;

import java.io.File;

import groovy.transform.CompileStatic;


@Canonical
@CompileStatic
public interface ProgramInfo {


    File getRunFile();

    boolean matches(String cmd);

    String name();


    void runProcess();

    boolean allowManyProcessesMatched();

}
