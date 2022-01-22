package net.sf.jremoterun.utilities.nonjdk.python

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.python.core.PyBoolean
import org.python.core.PyList
import org.python.core.PyObject
import org.python.core.PyString
import org.python.util.PythonInterpreter;

import java.util.logging.Logger;

@CompileStatic
class FileDiff {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public PythonInterpreter pythonInterpreter = new PythonInterpreter();


    void init1(int tabSize,int wrapcolumn){
pythonInterpreter.exec """

import difflib

hd = difflib.HtmlDiff(tabsize=${tabSize}, wrapcolumn=${wrapcolumn})

"""
    }

    String doWork(List<String> f1,List<String> f2,String file1,String file2,boolean context){
        pythonInterpreter.set('file1',new PyString(file1));
        pythonInterpreter.set('file2',new PyString(file2));
        pythonInterpreter.set('cont1',new PyList(f1));
        pythonInterpreter.set('cont2',new PyList(f2));
        //pythonInterpreter.set('conx21',new PyBoolean(context));
        pythonInterpreter.exec 'myres = hd.make_file(cont1,cont2,file1,file2,context=True)'
        PyObject r22 = pythonInterpreter.get('myres')
        return r22.asString()
    }



//    void f1(){
//
//        String f1
//        String f2
//        pythonInterpreter.set('cont1',new PyList(f1.readLines()));
//        pythonInterpreter.set('cont2',new PyList(f2.readLines()));
////        hd = difflib.HtmlDiff(tabsize=4, wrapcolumn=80)
////
////        myres = hd.make_file(cont1,cont2,"file1","file2", context=True)
//
//        pythonInterpreter.exec '''
//import difflib
//
//hd = difflib.HtmlDiff()
//
//myres = hd.make_file(cont1,cont2,"file1","file2")
//
//'''
//        PyObject r22 = pythonInterpreter.get('myres')
//
//        log.info "2"
//        log.info "${r22.asString()}"
//    }


}
