package net.sf.jremoterun.utilities.nonjdk.linux

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LinuxProcess {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public long pid;

    LinuxProcess(long pid) {
        this.pid = pid
    }

    File getProcessDir() {
        File f = new File("/proc/${pid}")
        return f;
    }

    File getFile(LinuxProcessFileLayout l) {
        File f = new File(getProcessDir(), l.customName)
        return f
    }

    List<String> getFullCmd() {
        File f = getFile(LinuxProcessFileLayout.cmdline)
        if (!f.exists()) {
            throw new FileNotFoundException(f.getAbsolutePath())
        }
        String text1 = f.text
        char cc = '\0'
        List<String> tokenize1 = text1.tokenize(cc)
        return tokenize1
    }

    static List<String> ignoreDirs = ['0', '1', '2']

    static List<LinuxProcess> getAllAccessibleProcesses() {
        File file1 = new File('/proc')
        List<File> list1 = file1.listFiles().toList()
        list1 = list1.findAll { it.isDirectory() }
        list1 = list1.findAll { !ignoreDirs.contains(it.getName()) }
        list1 = list1.findAll { it.getName().isNumber() }
        List<LinuxProcess> linuxProcesses = list1.collect { new LinuxProcess(it.getName() as long) }
        linuxProcesses = linuxProcesses.findAll { it.getFile(LinuxProcessFileLayout.exe).canRead()};
        linuxProcesses = linuxProcesses.findAll {
            File f = it.getFile(LinuxProcessFileLayout.cmdline);
            return f.exists() && f.canRead() && f.text.length() > 1
        }
        return linuxProcesses
    }

}
