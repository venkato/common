package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.fileloayout.Java11ModuleStructureFile
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.fife.rsta.ac.java.PackageMapNode
import org.fife.rsta.ac.java.buildpath.LibraryInfo
import org.fife.rsta.ac.java.classreader.ClassFile

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
public class JModLibraryInfo extends LibraryInfo {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public final File jmodFile;
    public ZipFile zipFileO;

    public static String classesPrefix = Java11ModuleStructureFile.classes3.customName+"/";

    public JModLibraryInfo(File jmodFile) {
        assert jmodFile.exists()
        this.jmodFile = jmodFile;
    }

    @Override
    public void bulkClassFileCreationEnd() {
        zipFileO.close();
    }

    @Override
    public void bulkClassFileCreationStart() {
        zipFileO = new ZipFile(jmodFile);
    }

    @Override
    public int compareTo(LibraryInfo info) {
        if (this.is(info)) {
            return 0;
        }
        int result = -1;
        if (info instanceof JModLibraryInfo) {
            result = jmodFile.compareTo(info.jmodFile);
        }
        return result;
    }

    @Override
    public ClassFile createClassFile(String entryName) throws IOException {
        JarFile jar = new JarFile(jmodFile);
        try  {
            return createClassFileImpl(jar, entryName);
        }finally {
            JrrIoUtils.closeQuietly (jar)
        }
    }

    @Override
    public ClassFile createClassFileBulk(String entryName) throws IOException {
        return createClassFileImpl(zipFileO, entryName);
    }


    ClassFile createClassFileImpl(ZipFile jar, String entryName) throws IOException {
        ZipEntry entry = jar.getEntry(classesPrefix + entryName);
        if (entry == null) {
            return null;
        }
        DataInputStream inn = new DataInputStream(new BufferedInputStream(jar.getInputStream(entry)));
        try {
            return new ClassFile(inn);
        } finally {
            JrrIoUtils.closeQuietly2(inn, log)
        }
    }

    @Override
    public PackageMapNode createPackageMap() throws IOException {
        PackageMapNode root = new PackageMapNode();
        JarFile jar = new JarFile(jmodFile);
        try  {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(ClassNameSuffixes.dotclass.customName) && entryName.startsWith(classesPrefix)) {
                    root.add(entryName.substring(classesPrefix.length()));
                }
            }
        }finally {
            JrrIoUtils.closeQuietly (jar)
        }
        return root;
    }

    @Override
    public long getLastModified() {
        return jmodFile.lastModified();
    }

    @Override
    public String getLocationAsString() {
        return jmodFile.getAbsolutePath();
    }

    @Override
    public int hashCode() {
        return jmodFile.hashCode();
    }

    @Override
    public int hashCodeImpl() {
        return jmodFile.hashCode();
    }

//    @Override
//    boolean equals(Object o) {
//        if (o instanceof JModLibraryInfo) {
//            File file1 = o.jmodFile
//            return file1 == jmodFile
//        }
//        return false
//    }


}