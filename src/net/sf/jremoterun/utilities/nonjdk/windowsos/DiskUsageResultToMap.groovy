package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.generalutils.PeriodToStringHumanConverter

import java.text.DecimalFormat
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class DiskUsageResultToMap {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //{ 'name':[  size,[]  ] }

    boolean sortBySize = true

    public static long oneMg = 1_000_000
//    public static long tenMg = long.valueOf(1_000_000)
    public static long oneGg = 1_000_000_000
//    public static long tenGg = long.valueOf(10_000_000_000)
    public DecimalFormat decimalFormatDecimal = new DecimalFormat('#.##')
    public DecimalFormat decimalFormatWholeNumber = new DecimalFormat('#')

    String writeSize(long bd) {
        if (bd > oneGg) {
            BigDecimal divide1 = bd / oneGg
            return "${decimalFormatDecimal.format(divide1)} gb"
        }

        BigDecimal divide1 = bd / oneMg
        return "${decimalFormatDecimal.format(divide1)} mb"
    }

    public PeriodToStringHumanConverter periodToStringHumanConverter = new PeriodToStringHumanConverter()

    Map buildStat(DiskUsage2Log stat) {
        Map r = [:]
        r.put('start date', new SimpleDateFormat('yyyy-MM-dd HH:mm').format(stat.startDate))
        long duration = System.currentTimeMillis() - stat.startDate.getTime()
        r.put('duration raw', duration)
        r.put('duration human', periodToStringHumanConverter.logDuration(duration))
        r.put('counted files', decimalFormatWholeNumber.format(stat.totalCheckFiles))
        r.put('counted dirs', decimalFormatWholeNumber.format(stat.totalCheckDirs))
        r.put('sleep count', decimalFormatWholeNumber.format(stat.sleepCount))
        r.put('ignored files count', decimalFormatWholeNumber.format(stat.ignoredFilesCount))
        r.put('big size', decimalFormatWholeNumber.format(stat.bigSize1))
        Map<String,String> erros2 = [:]
        stat.errors.each {
            erros2.put(it.key.getAbsolutePath().replace('\\','/') , it.getValue())
        }
        erros2= erros2.sort()
        r.put('error files', erros2)
        return r;
    }

    public int needAggregateSmallFilesIfCountMore = 6
    public int needAggregateFilesSizeLessInMg = 10

    Map convert(DiskUsageResult aa) {
        String name1 = aa.f.getName()
        List<DiskUsageResult> childs2 = aa.childs
        if (childs2 == null) {
            Map r = [:]
            r.put(name1, writeSize(aa.totalSize))
            return r;
        }
        List values = []
        values.add(writeSize(aa.totalSize))
        if (sortBySize) {
            childs2 = childs2.sort()
        }
        int smallFilesShowed = 0
        int smallFilesCount = 0
        long otherSmallFilesSize = 0 ;
        int needAggregateSmallFilesIfCountMore1 = isNeedAggregateIfCountLess(aa)
        if(childs2.size()>needAggregateSmallFilesIfCountMore1){
            childs2 = childs2.findAll {
                boolean b = isNeedAggregateFile(it)
                if(b){
                    if(smallFilesShowed<needAggregateSmallFilesIfCountMore1){
                        smallFilesShowed++
                        return true
                    }else {
                        smallFilesCount++
                        otherSmallFilesSize += it.totalSize
                    }
                }else{
                    smallFilesShowed++
                }

                return !b
            }
        }
        values.addAll childs2.collect { convert(it) }
        if(smallFilesCount>0){
            Map mm = [:]
            mm.put(othersElName,writeSize(otherSmallFilesSize))
            values.add(mm)
        }
        Map r = [:]
        r.put(name1, values)
        return r;
    }

    int isNeedAggregateIfCountLess(DiskUsageResult aa){
        return needAggregateSmallFilesIfCountMore
    }

    public String othersElName = 'othersSmall'

    boolean isNeedAggregateFile(DiskUsageResult e){
        return e.totalSize <(needAggregateFilesSizeLessInMg*1000_0000)
    }


    void writeResult(File f, int rotateCount,DiskUsageResult r,DiskUsage2Log stat){
        Map stat2 = buildStat(stat)
        stat2.put('full path',r.f.getAbsolutePath().replace('\\','/'))
        stat2.put('total length',r.totalSize)
        Map map = convert(r)
        map.put('stat',stat2)
        FileRotate.rotateFile(f,rotateCount)
        writeToFile(f,map)
    }

    void writeToFile(File f, DiskUsageResult r) {
        writeToFile(f, convert(r))
    }

    void writeToFile(File f, Map map) {
        String json = JsonOutput.toJson(map)
        json = JsonOutput.prettyPrint(json)
        try {
            f.text = json
        }catch(Exception e){
            log.info "${json}"
            throw e
        }
    }

}
