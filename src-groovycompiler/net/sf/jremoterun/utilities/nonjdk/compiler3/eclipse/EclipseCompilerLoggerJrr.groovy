package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.internal.compiler.batch.Main;

import java.util.logging.Logger;

@CompileStatic
class EclipseCompilerLoggerJrr extends Main.Logger{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseCompiler3 eclipseCompiler3

    EclipseCompilerLoggerJrr(EclipseCompiler3 main, PrintWriter out, PrintWriter err) {
        super(main, out, err)
        this.eclipseCompiler3 = main
    }

    @Override
    void logException(Exception e) {
        if(eclipseCompiler3.throwExceptionIfOccured){
            throw e;
        }else {
            super.logException(e)
        }

    }
}
