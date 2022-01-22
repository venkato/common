package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthOption
import org.apache.http.auth.AuthScheme
import org.apache.http.auth.MalformedChallengeException
import org.apache.http.impl.client.ProxyAuthenticationStrategy
import org.apache.http.protocol.HttpContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class ProxyAuthenticationStrategyJrr extends ProxyAuthenticationStrategy{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level1 = Level.INFO

    @Override
    boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        return super.isAuthenticationRequested(authhost, response, context)
    }

    @Override
    Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Map<String, Header> challenges = super.getChallenges(authhost, response, context)
        log.log(level1, "${authhost} ${challenges}")
        return challenges
    }

    @Override
    Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Queue<AuthOption> select = super.select(challenges, authhost, response, context)
        log.log(level1, "${challenges} ${select}")
        return select
    }

    @Override
    void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        log.log(level1, "good ${authhost}")
        super.authSucceeded(authhost, authScheme, context)
    }

    @Override
    void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        log.log(level1, "failed ${authhost}")
        super.authFailed(authhost, authScheme, context)
    }
}
