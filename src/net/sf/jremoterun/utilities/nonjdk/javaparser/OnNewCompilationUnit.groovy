package net.sf.jremoterun.utilities.nonjdk.javaparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.logging.Logger;

@CompileStatic
interface OnNewCompilationUnit {
    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void onNewCompilationUnit(File sourceFilePath, CompilationUnit ast)



}
