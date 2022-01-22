package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.errors.UnsupportedCredentialItem
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish;

import java.util.logging.Logger;

@CompileStatic
class CredentialsProviderWrapper extends CredentialsProvider{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public CredentialsProvider credentialsProviderNested;

    CredentialsProviderWrapper(CredentialsProvider credentialsProviderNested) {
        this.credentialsProviderNested = credentialsProviderNested
    }

    @Override
    boolean isInteractive() {
        return credentialsProviderNested.isInteractive()
    }

    @Override
    boolean supports(CredentialItem... items) {
        return credentialsProviderNested.supports(items)
    }

    @Override
    boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        return credentialsProviderNested.get(uri,items)
    }

    @Override
    boolean get(URIish uri, List<CredentialItem> items) throws UnsupportedCredentialItem {
        return credentialsProviderNested.get(uri,items)
    }

    @Override
    void reset(URIish uri) {
        credentialsProviderNested.reset(uri)
    }


}
