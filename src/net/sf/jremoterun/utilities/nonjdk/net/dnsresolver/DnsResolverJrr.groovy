package net.sf.jremoterun.utilities.nonjdk.net.dnsresolver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.xbill.DNS.Name
import org.xbill.DNS.Record
import org.xbill.DNS.Type
import org.xbill.DNS.lookup.LookupResult
import org.xbill.DNS.lookup.LookupSession;

import java.util.logging.Logger;

@CompileStatic
class DnsResolverJrr {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LookupSession s
    public static String suffix = '.in-addr.arpa'

    void prepare() {
        s = LookupSession.defaultBuilder().build();
    }

    String convertIpv4ToRequest(String addr){
        List<String> tokenize = addr.tokenize('.')
        assert tokenize.size()==4
        return tokenize.reverse().join('.')+suffix
    }



    List<org.xbill.DNS.PTRRecord > resolveDnsByIpv4Human(String addrSpecific) {
        return resolveDnsByIpv4Impl(Name.fromString(convertIpv4ToRequest(addrSpecific)))
    }

    List<org.xbill.DNS.PTRRecord > resolveDnsByIpv4Human2(String addrSpecific) {
        return resolveDnsByIpv4Impl(org.xbill.DNS.ReverseMap.fromAddress(addrSpecific))
    }

    /**
     * @see org.xbill.DNS.spi.DnsjavaInetAddressResolver#lookupByAddress
     * @see org.xbill.DNS.spi.DNSJavaNameService#getHostByAddr
     * @see org.xbill.DNS.Address#getHostName
     */
    List<org.xbill.DNS.PTRRecord > resolveDnsByIpv4Impl(Name mxLookup) {
        LookupResult lookupResult = s.lookupAsync(mxLookup, Type.PTR)
                .toCompletableFuture()
                .get();
        List<Record> records = lookupResult.getRecords()
        return (List)records
    }


}
