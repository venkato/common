package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@CompileStatic
class UrlCLassLoaderUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String convertFile(File filed1){
        String filePath1 = filed1.getCanonicalFile().getAbsolutePath().replace('\\', '/')
        if(!filePath1.startsWith('/')){
            filePath1 ='/'+filePath1
        }
        String res = 'file:' + filePath1;
        if (filed1.isDirectory()) {
            res += '/'
        }
        return res;
    }

    public static void createJarForLoadingClassesF(List<File> files, File destFile) throws Exception {
        assert destFile.getParentFile().exists()
        byte[] bs = UrlCLassLoaderUtils2.createJarForLoadingClasses(files.unique())
        destFile.bytes = bs
    }

    public static int manifestPositionAdd = 70

    static String realign(final String classPath ){
        List<String> r = []
        int pos = 0
        while (true) {
            int next = pos + manifestPositionAdd
            if (next >= classPath.length()) {
                r.add(classPath.substring(pos));
                break
            } else {
                r.add(classPath.substring(pos, next))
                pos = next
            }
        }
        return r.join('\n ')
    }

    static String createManifestContent(List<File> files){
        String classPath = files.collect {convertFile(it)}.join(' ')
        final String classPath2 = realign("Class-Path: ${classPath}")
        String content =
                """Manifest-Version: 1.0
${classPath2}
""".replace('\r\n', '\n').replace('\n', '\r\n')
        return content
    }

    public static byte[] createJarForLoadingClasses(List<File> files) throws Exception {
        String content = createManifestContent(files)
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream outZip = new ZipOutputStream(out);
        final ZipEntry outZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
        outZip.putNextEntry(outZipEntry);
        outZip.write(content.getBytes());
        outZip.closeEntry();
        outZip.finish();
        outZip.flush();
        out.close();
        return out.toByteArray()
    }

    static List<File> getClassLocationAll2(final Class clazz) {
        return getClassLocationAll2( new ClRef(clazz),clazz.getClassLoader())
    }

    /**
     * return without tail
     */
    static List<File> getClassLocationAll2(final ClRef clazz,ClassLoader cll) {
        final String tailJava = clazz.getClassPath() + ClassNameSuffixes.dotclass.customName;
        final String tailGroovy = clazz.getClassPath() + ClassNameSuffixes.dotgroovy.customName;
        List<URL> urls = getClassLocationAll(clazz.className, cll)
        // log.info "urls = ${urls}"
        List<File> files = urls.collect {
            String asStr = it.toString()
            try {
                return convertUrlToFile(it, tailJava, tailGroovy)
            }catch(Throwable e){
                log.info("failed at ${asStr} ${e}")
                throw e;
            }
        }
        return files
    }

    static File convertUrlToFile(URL url1, final String tailJava,final String tailGroovy){
        final String asString1 = url1.toString();
        String asString;
        if (asString1.endsWith(ClassNameSuffixes.dotclass.customName)) {
            asString = asString1.substring(0, asString1.length() - tailJava.length());
        } else if (asString1.endsWith(ClassNameSuffixes.dotgroovy.customName)) {
            asString = asString1.substring(0, asString1.length() - tailGroovy.length());
        } else {
            throw new UnsupportedOperationException("strange file : ${url1}")
        }
        if (asString.startsWith("jar:")) {
            asString = asString.substring(4, asString.length() - 2);
        }
        URL url = new URL(asString)
        File res = UrlToFileConverter.c.convert(url)
        return res
    }

    /**
     * return without tail
     */
    static File getClassLocationFirst(final Class clazz) {
        return getClassLocationFirst( new ClRef(clazz),clazz.getClassLoader())
    }

    static File getClassLocationFirst(ClRef clazz,ClassLoader classLoader1) {
        List<File> all = getClassLocationAll2(clazz,classLoader1);
        if (all.size() == 0) {
            throw new FileNotFoundException("${clazz.className}")
        }
        return all.first();
    }

    static File convertFullPathToRootDotClass(final URL pathFull, String className) {
//        int dotCount = className.count('.')
        String asString = pathFull.toString();
        String suffix = UrlCLassLoaderUtils.buildClassNameSuffix(className)
        asString = asString.substring(0, asString.length() - suffix.length());
//        asString.findLastIndexOf {}
        if (asString.startsWith("jar:")) {
            asString = asString.substring(4, asString.length() - 2);

        }
        URL url = new URL(asString)
        File file3 = UrlToFileConverter.c.convert url
        return file3

    }


    static File convertFullPathToRootDotGroovy(final URL pathFull, String className) {
//        int dotCount = className.count('.')
        String asString = pathFull.toString();
        String suffix = UrlCLassLoaderUtils.buildClassNameSuffixGroovy(className)
        asString = asString.substring(0, asString.length() - suffix.length());
//        asString.findLastIndexOf {}
        if (asString.startsWith("jar:")) {
            asString = asString.substring(4, asString.length() - 2);

        }
        URL url = new URL(asString)
        File file3 = UrlToFileConverter.c.convert url
        return file3

    }

    /**
     * return without tail
     */
    static File convertClassLocationToPathToJar(URL urlRes, final String tail) {
        if (urlRes == null) {
            throw new NullPointerException("class location is null for ${tail}")
        }
        String url = urlRes.toString();
        url = url.substring(0, url.length() - tail.length());
        if (url.startsWith("jar:")) {
            url = url.substring(4, url.length() - 2);
        }
        URL url2 = new URL(url)
        File file3 = UrlToFileConverter.c.convert url2
        return file3
    }

    /**
     * Return locations with tail
     */
    static List<URL> getClassLocationAll(final String className, ClassLoader classLoader)
            throws MalformedURLException {
        if(className == Class.getName()){
            throw new IllegalArgumentException("Strange class name : ${className}")
        }
        final String tail = UrlCLassLoaderUtils.buildClassNameSuffix(className);
        if (classLoader == null) {
            log.info("class loaded by boot class loader : ${className}, finding any resource instead of all")
            Class<?> clazz1 = classLoader.loadClass(className);
            assert clazz1!=null
            URL res = JrrUtils.getClassLocation(clazz1)
            return [res]
        }
        Enumeration<URL> resources = classLoader.getResources(tail);
        List<URL> list = resources.toList()
        final String tailGroovy = UrlCLassLoaderUtils.buildClassNameSuffixGroovy(className);
        list.addAll(classLoader.getResources(tailGroovy).toList());
        return list
    }


    static void printSpi(Class spi) {
        printSpi(spi.getName())
    }

    static void printSpi(String spi) {
        Enumeration<URL> services = JrrClassUtils.currentClassLoader.getResources("META-INF/services/${spi}")
        List<URL> list = services.toList()
        log.info "found ${list.size()} spi : ${list}"
        list.each {
            log.info "${it}:\n${it.text}"
        }

    }

}
