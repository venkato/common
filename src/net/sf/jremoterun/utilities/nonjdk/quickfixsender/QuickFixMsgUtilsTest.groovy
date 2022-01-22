package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic
import junit.framework.TestCase;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class QuickFixMsgUtilsTest extends TestCase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void test11(){
        QuickFixMsgUtils msgUtils = new QuickFixMsgUtils()
        msgUtils.msg = "|8=FIX.4.4|9=123|35=A|49=sender|56=target|34=1|52=20230601-12:00:00.000|98=0|108=30|10=123"
        assert msgUtils.isTagSet(8)
        assert msgUtils.isTagSet(9)
        assert !msgUtils.isTagSet(123)
        assert msgUtils.isTagSet(10)

        msgUtils.removeTag(8, true)
        assert !msgUtils.isTagSet(8)
        msgUtils.removeTag(10, false)
        assert !msgUtils.isTagSet(10)
        assert !msgUtils.msg.contains("10=")
    }


    void test1(){
        QuickFixMsgUtils msgUtils = new QuickFixMsgUtils()
        msgUtils.msg = "8=FIX.4.4|9=123|35=A|49=sender|56=target|34=1|52=20230601-12:00:00.000|98=0|108=30|10=123|"
        assert msgUtils.isTagSet(8)
        assert msgUtils.isTagSet(10)
        assert msgUtils.isTagSet(108)
        assert !msgUtils.isTagSet(1118)
        msgUtils.removeTag(108,true)
        assert !msgUtils.isTagSet(108)
        assert !msgUtils.msg.contains("108=")
        msgUtils.replaceTagValue(98, "1",true)
        assert msgUtils.msg.contains("98=1")
        assert !msgUtils.msg.contains("1108=")
        try {
            msgUtils.removeTag(1108, true)
            fail()
        }catch (FixTagNotFoundException e){
            log.info("ok")
        }
        msgUtils.removeTag(1108, false)
        try {
            msgUtils.replaceTagValue(1108, 'sdfsdf', true)
            fail()
        }catch (FixTagNotFoundException e){
            log.info("ok")
        }
        msgUtils.replaceTagValue(1108, 'sdfsdf', false)
        assert msgUtils.msg == '8=FIX.4.4|9=123|35=A|49=sender|56=target|34=1|52=20230601-12:00:00.000|98=1|10=123|'
        //log.info "${msgUtils.msg}"
    }

    void test2(){
        QuickFixMsgUtils msgUtils = new QuickFixMsgUtils()
        msgUtils.msg = "8=FIX.4.4|9=123|35=A|49=sender|56=target|34=1|52=20230601-12:00:00.000|98=0|108=30|10=123|"
        String value = msgUtils.getTagValue(49)
        assert value == "sender"
    }

    void test3(){
        QuickFixMsgUtils msgUtils = new QuickFixMsgUtils()
        msgUtils.msg = "8=FIX.4.4|9=123|35=A|49=sender|56=target|34=1|52=20230601-12:00:00.000|197=4|97=1|1197=3|98=0|108=30|10=123|"
        String value = msgUtils.getTagValue(97)
        assert value == "1"
        msgUtils.removeTag(108, true)
        assert !msgUtils.msg.contains("108=")
        msgUtils.removeTag(1197, true)
        assert !msgUtils.msg.contains("1197=")
    }


}
