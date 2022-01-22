package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.x509

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.crypto.CertificateChecker
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils

import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class CertificateCheckerJrrHttp {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrHttpUtils jrrHttpUtils;
    public String urlOrHost;
    public CertificateChecker certificateChecker;
    public List<X509Certificate> certificateList
    public List<X509Certificate> certificateParentFound = []
    public List<X509Certificate> certificateParentNotFound = []

    CertificateCheckerJrrHttp(JrrHttpUtils jrrHttpUtils, String urlOrHost, CertificateChecker certificateChecker) {
        this.jrrHttpUtils = jrrHttpUtils
        this.urlOrHost = urlOrHost
        this.certificateChecker = certificateChecker
    }

    void doAll() {
        certificateList = jrrHttpUtils.getCertificatesFromHost(urlOrHost.toString())
        checkCertificate()
    }

    void checkCertificate() {
        X509Certificate x509Certificate = findRootCertificate()
        certificateChecker.checkCertificate(x509Certificate)
    }


    X509Certificate findRootCertificate() {
        log.info "found cert : ${certificateList.size()}"
        Map<String, X509Certificate> entries1 = certificateList.collectEntries { [(certificateChecker.getCommonName(it.getSubjectDN())): it] }
        X509Certificate rootCert
        certificateList.each {
            String issueDn1 = certificateChecker.getCommonName(it.getIssuerDN())
            String subjectDN1 = certificateChecker.getCommonName(it.getSubjectDN())
            X509Certificate get1 = entries1.get(issueDn1)
            if (get1 == null) {
                certificateParentNotFound.add(it)
            } else {
                certificateChecker.verifyCertificateByParent(it, get1)
                certificateParentFound.add(it)
            }
            if (subjectDN1 == issueDn1) {
                rootCert = it
            }
        }
        int size1 = certificateParentNotFound.size()
        if (size1 > 1) {
            List<String> certNames = certificateParentNotFound.collect { certificateChecker.getCommonName(it.getSubjectDN()) }
            throw new Exception("Too many root certs ${size1} ${certNames} :  ${certificateParentNotFound.join('\n\n')}")
        }

        if (rootCert == null) {
            if (size1 == 0) {
                throw new Exception("Can't find root cert")
            }
            return certificateParentNotFound[0]
        } else {
            if (size1 > 0) {
                X509Certificate certificate4 = certificateParentNotFound[0]
                throw new Exception("Found dup root cert : ${certificateChecker.getCommonName(rootCert.getSubjectDN())}  ${certificateChecker.getCommonName(certificate4.getSubjectDN())} :   \n ${rootCert}  vs \n ${certificate4}")
            }
            return rootCert
        }


    }
}
