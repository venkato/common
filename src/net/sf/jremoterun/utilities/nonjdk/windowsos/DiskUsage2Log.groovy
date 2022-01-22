package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.rstarunner.StatusDelayListener

import java.nio.file.Files
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Uses file length cmd
 * @see org.apache.commons.io.FileUtils#sizeOfDirectoryAsBigInteger
 */
@CompileStatic
abstract class DiskUsage2Log implements Closeable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public volatile boolean isCanContinue = true
    public volatile File lastCheckedFile
    public volatile long totalCheckFiles = 0
    public volatile long totalCheckDirs = 0
    public volatile long ignoredFilesCount = 0
    public volatile long totalDiskOperations = 0
    public volatile long sleepAfterDiskOperations = 10000
    public volatile long sleepTimeInMs = 100
    public volatile int sleepCount = 0
    public volatile boolean doSymbolicLinkCheck = false
    public Date startDate = new Date()
    public final Object lockObject = new Object()
    public StatusDelayListener delayListener;
    public Map<File, String> errors = [:];
    public long bigSize1 = 1_000_000_000
    public int maxDeep = 64;
    public HashSet<File> ignorePath = []
    public Collection<File> ignoreErrorPathStartWith = []
    public Map<File,Long> expectedDiskUsage = [:]
    public Map<File,Long> actualDiskUsage = [:]


    Set<File>  analizeDiskUsage(){
        Set<File> exectedF = new HashSet<>( expectedDiskUsage.keySet())
        Set<File> actualF = actualDiskUsage.keySet()
        exectedF.removeAll(actualF)
        return exectedF;
    }


    DiskUsageResult calcUsage(File f) {
        calcUsageImpl(f,0)
    }

    DiskUsageResult calcUsageImpl(File f,int deep) {
        if(deep>maxDeep){
            throw new Exception("too big deep ${deep} for ${f}")
        }
        try {
            if (delayListener != null) {
                delayListener.setStatusWithDelay(f.getAbsolutePath())
            }
            lastCheckedFile = f
            if (f.isFile()) {
                totalCheckFiles++
                totalDiskOperations++
                return new DiskUsageResult(f, calcFileUsage(f))
            }
            if (!f.isDirectory()) {
                return new DiskUsageResult(f,exceptionUnknownFileType(f))
            }
            totalCheckDirs++
            totalDiskOperations++
            List<File> list;
            try {
                File[] files1 = f.listFiles()
                if(files1==null){
                    list= onExceptionListFileNull(f)
                }else {
                    list = files1.toList()
                }
            } catch (Exception e) {
                list = exceptionOnListFiles(f, e)
            }
            list = list.findAll { isNeedInclude(it) }
            long res = 0
            List<DiskUsageResult> mappp = []
            list.each {
                if (isCanContinueF(it)) {
                    if(totalDiskOperations> sleepAfterDiskOperations){
                        totalDiskOperations = 0
                        synchronized (lockObject) {
                            lockObject.wait(sleepTimeInMs)
                        }
                        sleepCount++
                    }
                    DiskUsageResult usage1 = calcUsageImpl(it,deep+1)
                    mappp.add(usage1)
                    res = res+ usage1.totalSize
                }
            }

            boolean includeDetails = onResultReady(f, res, mappp)
            if (includeDetails) {
//                log.info "save details : ${f}"
                return new DiskUsageResult(f, res, mappp);
            }
            return new DiskUsageResult(f, res)
        }catch(Throwable e ){
            return exceptionGeneral(f,e)
        }
    }

    List<File> onExceptionListFileNull(File f){
        errors.put(f,'failed list file')
        return []
    }


    boolean  isCanContinueF(File f){
        return isCanContinue
    }


    long calcFileUsage(File f){
        try {
            return f.length();
        }catch(Exception e){
            return exceptionOnLengthFile(f,e)
        }
    }





    boolean onResultReady(File f, long totalSize, List<DiskUsageResult> mappp){
        Long expectedUsage = expectedDiskUsage.get(f)
        if(expectedUsage!=null){
            if(totalSize> expectedUsage){
                onUsageExceeded(f,totalSize)
            }
            actualDiskUsage.put(f,totalSize)
//            log.info "cp1 ${expectedUsage}"
            return true
        }
        if(totalSize>bigSize1){
//            log.info "cp2 ${totalSize}"
            return true
        }
        return false
    }

    abstract void onUsageExceeded(File f, long usageNow);

    public String fileName = 'diskUsage.txt'

//    public Comparator aa = new ReverseComparator()

    public  java.text.SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH');

    void writeReport(File f, List<DiskUsageResult> mappp){
        mappp = mappp.sort()
        List<String> els2 = ["${sdf.format(new Date())}".toString()]
        els2.addAll mappp.collect { "${it.totalSize} ${it.f.getName()}".toString() }
        File ffff = new File(f,fileName)
        ffff.text = els2.join('\n')
        //els.add(0,"")
    }

    DiskUsageResult exceptionGeneral(File f, Throwable e) {
        log.info "failed on ${f} ${e}"
        throw e
    }

    long exceptionUnknownFileType(File f) {
        errors.put(f,'unknown file')
        return 0
    }

    long exceptionOnLengthFile(File f, Exception e) {
        log.info "failed length : ${f}"
        throw e
    }


    List<File> exceptionOnListFiles(File f, Exception e) {
        log.info "failed list files for : ${f}"
        throw e;
    }

    boolean isNeedInclude(File f) {
        if (!isCanContinueF(f)) {
            return false
        }
        if(ignorePath.contains(f)){
            log.info "ignoring ${f}"
            ignoredFilesCount++
            return false
        }

        if(doSymbolicLinkCheck) {
            totalDiskOperations++
            boolean isSymling = isSymlink(f)
            if (isSymling) {
                return false
            }
        }
        return true
    }

    boolean isSymlink(final File file) throws IOException {
        return Files.isSymbolicLink(file.toPath());
    }


    Map<File, String> findErrorsFiltered(){
        return errors.findAll {!isCanIgnoreError(it.key)}
    }

    boolean isCanIgnoreError(File f){
        if(ignoreErrorPathStartWith.contains(f)){
            return true
        }
        boolean canIgnore =  ignoreErrorPathStartWith.find {it.isChildFile(f)} !=null
        return canIgnore
    }

    void normalizePaths(){
        ignorePath = new HashSet<>(ignorePath.collect {it.getCanonicalFile().getAbsoluteFile()})
        ignoreErrorPathStartWith = new HashSet<>(ignoreErrorPathStartWith.collect {it.getCanonicalFile().getAbsoluteFile()})
    }


    @Override
    void close() throws IOException {
        isCanContinue =false
        lockObject.notifyAll()
    }

}
