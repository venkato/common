package net.sf.jremoterun.utilities.nonjdk.lang

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.Java5VM
import net.sf.jremoterun.utilities.JavaVM;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef

import javax.management.ObjectName
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * operation=compilerCodelist http://www.onemusicapi.com/blog/2021/01/13/understanding-code-cache-listing/
 * https://stackoverflow.com/questions/41891127/jcmd-where-can-i-find-complementary-information
 * @see sun.management.DiagnosticCommandImpl
 * @see com.sun.management.internal.DiagnosticCommandImpl
 */
@CompileStatic
class JavaDiagnostic2Options {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName objectName = new ObjectName('com.sun.management:type=DiagnosticCommand')

    public static class Option1 {
        Boolean value;
        String name1;

        Option1(Boolean value, String name1) {
            this.value = value
            this.name1 = name1
        }
    }


    public Object objs;
    public Method method;

    JavaDiagnostic2Options() {
        Java5VM m = JavaVM.javaVM as Java5VM
        objs = m.getMBeanObject(objectName)
        method = JrrClassUtils.findMethodByCount(objs.getClass(), 'executeDiagnosticCommand', 1)  //NOFIELDCHECK
        MethodRef methodRef = new MethodRef(new ClRef('sun.management.DiagnosticCommandImpl'), 'executeDiagnosticCommand', 1)
    }

    String r(List objs) {
        List<String> args2 = []
        objs.each {
            if (it instanceof Option1) {
                Option1 oo = it as Option1
                if (oo.value != null && oo.value == true) {
                    args2.add(oo.name1)
                }
            } else {
                args2.add(it.toString())
            }
        }
        return executeCmd(args2.join(' '))
    }

    String executeCmd(String cmd){
        return method.invoke(objs,cmd)
    }


    //String VM_uptime(Boolean date) { r([ 'VM.uptime', '-date' ])   }
    //String VM_native_memory(Boolean summary, Boolean detail, Boolean baseline, Boolean summary_diff, Boolean detail_diff, Boolean shutdown, Boolean statistics, string scale) { r([ 'VM.native_memory', 'summary','detail','baseline','summary.diff','detail.diff','shutdown','statistics','scale' ])   }
    String GC_class_histogram(Boolean all) { r(['GC.class_histogram', new Option1(all, '-all')]) }

    String GC_run_finalization() { r(['GC.run_finalization',]) }

    void GC_class_stats_saveFile(String columns, Boolean all, Boolean csv, File outFile) {
        assert outFile!=null
        assert outFile.getParentFile().exists()
        outFile.text = GC_class_stats(columns,all,csv)
    }

    // return long string
    String GC_class_stats(String columns, Boolean all, Boolean csv) {
        List<Object> args = []
        args.add 'GC.class_stats'
        if (columns != null) {
            args.add('columns')
            args.add(columns)
        }
        args.add new Option1(all, '-all')
        args.add new Option1(csv, '-csv')
        return r(args)
    }

    String Thread_print(Boolean printWithLocks) { r(['Thread.print', new Option1(printWithLocks, '-l')]) }

    String GC_finalizer_info() { r(['GC.finalizer_info',]) }

    String VM_unlock_commercial_features() { r(['VM.unlock_commercial_features',]) }

    String GC_rotate_log() { r(['GC.rotate_log',]) }

    String VM_classloader_stats() { r(['VM.classloader_stats',]) }

    String GC_heap_info() { r(['GC.heap_info',]) }

    String GC_run() { r(['GC.run',]) }

    String VM_version() { r(['VM.version',]) }

    String VM_flags(Boolean all) { r(['VM.flags', new Option1(all, '-all')]) }

    String VM_command_line() { r(['VM.command_line',]) }

    String VM_dynlibs() { r(['VM.dynlibs',]) }


    /*
    Code that generate above :
    p1.collect{dump33(it)}.join('\n')


String dumpBody(Object el){
    return "'" +el.name+"', " + el.arguments.collect{"'${it.name}'" }.join(',') ;
}

String dump33(Object el){
    return el.name.replace('.','_') +'('+ el.arguments.collect{ "${it.type.toLowerCase()} ${it.name.replace('-','').replace('.','_')}" }.join(', ')+') { r([ '+  dumpBody(el) +' ])   } ; '
}

     */
}
