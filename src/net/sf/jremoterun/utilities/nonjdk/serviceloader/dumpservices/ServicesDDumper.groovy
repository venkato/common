package net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class ServicesDDumper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String servicePathPrefix = 'META-INF/services/'


    Map<File, Map<ClRef, List<ClRef>>> readServicesManyFiles(Collection<File> jars) {
        Map<File, Map<ClRef, List<ClRef>>> aa = [:]
        jars.each {
            if (it.isFile()) {
                Map<ClRef, List<ClRef>> services1 = readServices(it)
                if (services1.size() > 0) {
                    aa.put(it, services1)
                }
            }
        }
        return aa
    }

    Map<ClRef, List<ClRef>> readServices(ToFileRef2 f) {
        return readServices(f.resolveToFile())
    }

    Map<ClRef, List<ClRef>> readServices(File jarFile) {
        Map<ClRef, List<ClRef>> entries = [:]
        BufferedInputStream stream1 = jarFile.newInputStream()
        ZipInputStream z = new ZipInputStream(stream1)
        while (true) {
            ZipEntry nextEntry = z.getNextEntry()
            if (nextEntry == null) {
                break
            }
            String name1 = nextEntry.getName()
            // sshd-mina-2.10.0.jar!\META-INF\services\org.apache.sshd.common.io.IoServiceFactoryFactory
            if (name1.startsWith(servicePathPrefix)) {

                String serviceName1 = name1.replace(servicePathPrefix, '')
                if(serviceName1.length()>0) {
                    //log.info "checking = ${name1}"
                    byte[] array = IOUtils.toByteArray(z);
                    String s = new String(array)
                    List<ClRef> valuess = findServicesFromFile(s)
//                    log.info "${serviceName1} : ${valuess}"
                    entries.put(new ClRef(serviceName1), valuess)
                }
            }
        }
        return entries;
    }

    List<ClRef> findServicesFromFile(String s){
        return findServicesFromFile2(s).collect {new ClRef(it)};
    }

    List<String> findServicesFromFile2(String s){
        List<String> valuess = s.readLines().findAll { it != null }.collect { it.trim() }.findAll { it.length() > 0 }.findAll { !it.startsWith('#') }.collect {it.tokenize('#')[0]}.collect {it.trim()}
        return valuess
    }


}
