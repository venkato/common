package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


@Documented
@Retention(RetentionPolicy.RUNTIME)
@interface IgnoreFieldDumper {

}