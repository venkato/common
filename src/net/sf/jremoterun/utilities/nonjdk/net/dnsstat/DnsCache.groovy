package net.sf.jremoterun.utilities.nonjdk.net.dnsstat

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.net.dnsresolver.DnsResolverJrr
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.BackupLocationDateTime
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.BackupLocationI
import net.sf.jremoterun.utilities.nonjdk.store.csvstore.ListBeanReaderCsv
import net.sf.jremoterun.utilities.nonjdk.store.csvstore.ListBeanStoreCsv
import net.sf.jremoterun.utilities.nonjdk.store.csvstore.ObjectWriterCsvImpl
import org.xbill.DNS.ARecord
import org.xbill.DNS.Name
import org.xbill.DNS.PTRRecord
import org.xbill.DNS.lookup.NoSuchDomainException

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class DnsCache {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, DnsInfoBean> dnsInfoBeans = [:]
    public Date refetchOlderThen = DurationConstants.oneDay.moveDateFromNow(-30)
    public Date notStoreOlderThenOlderThen = DurationConstants.oneDay.moveDateFromNow(-60)

    public DnsResolverJrr dnsResolverJrr = new DnsResolverJrr();
    public boolean modified = false
    public File cacheFile
    public SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMdd-HHmm')
    public BackupLocationI backupLocationDateTime

    public List<String> takenFromCache = []
    public List<String> resolvedOk = []
    public List<String> timeoutHosts = []
    public List<String> retriedHosts = []
    public List<String> unknownHosts = []


    public int maxRetry = 1

    DnsCache(File cacheFile) {
        this.cacheFile = cacheFile
        backupLocationDateTime = new BackupLocationDateTime(cacheFile.getParentFile(), 'dnsCacheAr.txt')
        dnsResolverJrr.prepare()
        if (cacheFile.exists()) {
            readCache()
        }
    }

    String buildStat() {
        List<String> r = []
        addStat('takenFromCache', takenFromCache, r)
        addStat('resolvedOk', resolvedOk, r)
        addStat('timeoutHosts', timeoutHosts, r)
        addStat('retriedHosts', retriedHosts, r)
        addStat('unknownHosts', unknownHosts, r)

        return r.join(' ')
    }

    void addStat(String name, List<String> value, List<String> r) {
        if (!value.isEmpty()) {
            r.add "${name}=${value.size()}".toString()
        }
    }

    void readCache() {
        ListBeanReaderCsv beanReaderCsv = new ListBeanReaderCsv(DnsInfoBean)
        beanReaderCsv.objectReader.customConverters.put(Date, new net.sf.jremoterun.utilities.nonjdk.str2obj.DateConverter(sdf))
        List<DnsInfoBean> beans = beanReaderCsv.readList(cacheFile.readLines()) as List
        beans.each {
            DnsInfoBean a = it as DnsInfoBean
            dnsInfoBeans.put(a.hostName, a)
        }
    }

    void saveCacheIfNeeded() {
        if (modified) {
            saveCache()
        }
    }

    List<DnsInfoBean> getBeansToSave() {
        return dnsInfoBeans.values().findAll { it.lastResolved > notStoreOlderThenOlderThen } as List
    }

    void saveCache() {
        ListBeanStoreCsv beanStoreCsv = new ListBeanStoreCsv(DnsInfoBean)
        ObjectWriterCsvImpl objectWriterCsv = (ObjectWriterCsvImpl) beanStoreCsv.objectWriter
        objectWriterCsv.addAddCustomWriter2(Date, new net.sf.jremoterun.utilities.nonjdk.store.csvstore.CsvDateWriter(sdf))
        String s = beanStoreCsv.saveList(getBeansToSave())
        if (cacheFile.exists()) {
            backupLocationDateTime.backupFile(cacheFile)
        }
        cacheFile.text = s
    }

    Map<String, String> ip2namePredefined = ['127.0.0.1':'localhost']
    Map<String, String> name2ipPredefined = ['localhost':'127.0.0.1']

    String resolveByIpv(String ipAddr) {
        String predef2 = ip2namePredefined.get(ipAddr)
        if (predef2 != null) {
            return predef2
        }
        List<PTRRecord> hostName = dnsResolverJrr.resolveDnsByIpv4Human(ipAddr)
        if (hostName != null && !hostName.isEmpty()) {
            Name target = hostName[0].getTarget()
            return target.toString()
        }
        return null

    }

    String resolveHostName(String hostName) {
        String predef2 = name2ipPredefined.get(hostName)
        if (predef2 != null) {
            return predef2
        }
        List<ARecord> hostNames2 = dnsResolverJrr.resolveHostNames(hostName)
        if (hostName != null && !hostName.isEmpty()) {
            return hostNames2[0].getAddress().getHostAddress()
        }
        return null
    }

    DnsInfoBean decideOnException(String ipOrHost, int retryCount, Throwable e) {
        DnsInfoBean infoBean = new DnsInfoBean()
        infoBean.hostName = ipOrHost
        infoBean.lastResolved = new Date()

        Throwable cause123 = JrrUtils.getRootException(e)
        if (isTimeoutException(cause123)) {
            infoBean.resolvedStatus = DnsResolvedStatus.timeout
            timeoutHosts.add(ipOrHost)
            return infoBean
        }
        if (cause123 instanceof NoSuchDomainException) {
            log.info "unknown host ${ipOrHost}"
            unknownHosts.add(ipOrHost)
            infoBean.resolvedStatus = DnsResolvedStatus.notExisted
            return infoBean
        }
        throw e
    }


    boolean isNeedRefetch(DnsInfoBean infoBean) {
        if (infoBean.resolvedStatus in [DnsResolvedStatus.success, DnsResolvedStatus.notExisted,]) {
            if (infoBean.lastResolved > refetchOlderThen) {
                return false
            }
        }
        return true
    }

    String resolveGenericRetryWithCache2(String ipOrHost) {
        DnsInfoBean dnsInfoBean1 = resolveGenericRetryWithCache(ipOrHost)
        if (dnsInfoBean1 == null) {
            return null
        }
        if (dnsInfoBean1.resolvedStatus == DnsResolvedStatus.success) {
            return dnsInfoBean1.resolvedTo
        }
        return null
    }

    DnsInfoBean resolveGenericRetryWithCache(String ipOrHost) {
        DnsInfoBean fromCache1 = dnsInfoBeans.get(ipOrHost)
        if (fromCache1 != null) {
            if (isNeedRefetch(fromCache1)) {

            } else {
                takenFromCache.add(ipOrHost)
                return fromCache1
            }
        }
        DnsInfoBean result = resolveGenericRetryNoCache(ipOrHost)
        updateCacheIfNeeded(fromCache1, result)
        return result
    }

    void updateCacheIfNeeded(DnsInfoBean fromCache1, DnsInfoBean result) {
        boolean needPutToCache = true
        if (fromCache1 != null) {
            needPutToCache = isNeedUpdateCache(fromCache1, result)
        }
        if (needPutToCache) {
            modified = true
            dnsInfoBeans.put(result.hostName, result)
        }
    }


    boolean isNeedUpdateCache(DnsInfoBean fromCache1, DnsInfoBean result) {
        if (fromCache1.resolvedStatus != result.resolvedStatus) {
            return true
        }
        if (fromCache1.resolvedStatus in [DnsResolvedStatus.failed, DnsResolvedStatus.timeout,]) {
            return false
        }
        return true
    }

    DnsInfoBean resolveGenericRetryNoCache(String ipOrHost) {
        int retryCount = -1
        while (true) {
            retryCount++
            try {
                String generic1 = resolveGenericImpl(ipOrHost)
                resolvedOk.add(ipOrHost)
                DnsInfoBean infoBean = new DnsInfoBean()
                infoBean.hostName = ipOrHost
                infoBean.lastResolved = new Date()
                infoBean.resolvedTo = generic1
                infoBean.resolvedStatus = DnsResolvedStatus.success
                return infoBean
            } catch (Throwable e) {
                DnsInfoBean infoBean = decideOnException(ipOrHost, retryCount, e)
                boolean continue1 = false
                if (infoBean.resolvedStatus == DnsResolvedStatus.timeout) {
                    if (retryCount >= maxRetry) {
                        return infoBean
                    }
                    retriedHosts.add(ipOrHost)
                    log.info "timeout was for ${ipOrHost} ${e}, retrying"
                    continue1 = true
                }
                if (continue1) {

                } else {
                    return infoBean
                }
            }
        }
    }

    String resolveGenericImpl(String ipOrHost) {
        if (DnsResolverJrr.isIpAddr(ipOrHost)) {
            return resolveByIpv(ipOrHost)
        }
        return resolveHostName(ipOrHost)
    }


    boolean isTimeoutException(Throwable e) {
        return e.toString().contains('Timed out while trying to resolve')
    }

}
