package net.sf.jremoterun.utilities.nonjdk.idea.filelayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider


@CompileStatic
enum LibClassPathLib implements ChildRedirect, EnumNameProvider {
    annotations, 
    app,
    @Deprecated
    groovy, 
    intellij__d__charts, 
    intellij__d__java__d__aetherDependencyResolver, 
    intellij__d__libraries__d__asm__d__tools, 
    intellij__d__libraries__d__batik, 
    intellij__d__libraries__d__bouncy__d__castle__d__provider, 
    intellij__d__libraries__d__caffeine, 
    intellij__d__libraries__d__commons__d__cli, 
    intellij__d__libraries__d__commons__d__codec, 
    intellij__d__libraries__d__commons__d__io, 
    intellij__d__libraries__d__commons__d__lang3, 
    intellij__d__libraries__d__commons__d__text, 
    intellij__d__libraries__d__fastutil, 
    intellij__d__libraries__d__grpc, 
    intellij__d__libraries__d__guava, 
    intellij__d__libraries__d__http__d__client, 
    intellij__d__libraries__d__jackson__d__databind, 
    intellij__d__libraries__d__jediterm__d__core, 
    intellij__d__libraries__d__jediterm__d__ui, 
    intellij__d__libraries__d__jsoup, 
    intellij__d__libraries__d__kotlinx__d__coroutines__d__core, 
    intellij__d__libraries__d__kotlinx__d__serialization__d__core, 
    intellij__d__libraries__d__ktor__d__utils, 
    intellij__d__libraries__d__maven__d__resolver__d__provider, 
    intellij__d__libraries__d__netty__d__buffer, 
    intellij__d__libraries__d__netty__d__codec__d__http, 
    intellij__d__libraries__d__opencsv, 
    intellij__d__libraries__d__oro__d__matcher, 
    intellij__d__libraries__d__pty4j, 
    intellij__d__libraries__d__sshj, 
    intellij__d__libraries__d__winp, 
    intellij__d__libraries__d__xstream, 
    intellij__d__platform__d__analysis__d__impl, 
    intellij__d__platform__d__analysis, 
    intellij__d__platform__d__core__d__impl, 
    intellij__d__platform__d__core, 
    intellij__d__platform__d__core__d__ui, 
    intellij__d__platform__d__debugger__d__impl__d__ui, 
    intellij__d__platform__d__debugger, 
    intellij__d__platform__d__ide__d__core, 
    intellij__d__platform__d__ide__d__impl, 
    intellij__d__platform__d__ide, 
    intellij__d__platform__d__lang__d__core, 
    intellij__d__platform__d__lang__d__impl, 
    intellij__d__platform__d__lang, 
    intellij__d__platform__d__projectModel__d__impl, 
    intellij__d__platform__d__projectModel, 
    intellij__d__platform__d__util__d__ui, 
    intellij__d__settingsSync__d__core, 
    intellij__d__xml__d__xmlbeans, 
    lib__t__backend, 
    platform__t__loader, 
    trove, 
    util__t__8, 
    util, 
    util_rt, 

    ;

    String customName;

    ExactChildPattern ref;

    LibClassPathLib() {
        this.customName = name().replace('__d__','.').replace('__t__','-')+'.jar'
        ref = new ExactChildPattern('lib/'+customName)

    }


}
