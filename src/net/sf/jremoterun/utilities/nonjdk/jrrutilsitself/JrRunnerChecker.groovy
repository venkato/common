package net.sf.jremoterun.utilities.nonjdk.jrrutilsitself

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.StringFindRange
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload;

import java.util.logging.Logger;

@CompileStatic
class JrRunnerChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //List<String> propsStr = []
    public List<String> propsNameOrder = []
    public Map<String, String> props1 = [:];
    public static String beginS = '# begin constanats'
    public static String endS = '# ends constanats'
    public int beginC = -1;
    public int endC = -1;

    void loadProps(File f) {
        log.info "reading props from ${f}"
        List<String> lines1 = f.readLines()
        List<String> linesMacthed = []
        boolean collecting = false
        for (i in 0..<lines1.size()) {
            String cont1 = lines1[i]
            if (cont1.contains(endS)) {
                assert endC == -1
                endC = i
                collecting = false
            }
            if (collecting) {
                linesMacthed.add(cont1)
            }

            if (cont1.contains(beginS)) {
                assert beginC == -1
                beginC = i
                collecting = true
            }
        }
        handleMatchedLines(linesMacthed)
    }

    void handleMatchedLines(List<String> linesMacthed) {
        linesMacthed = linesMacthed.collect { normalizeLine(it) }.findAll { it != null }
        linesMacthed.each { parseLine(it) }
    }

    void parseLine(String s) {
//        StringFindRange findRange=new StringFindRange(s,'=')
        int of1 = s.indexOf('=')
        assert of1 > 1: s
        String name1 = s.substring(0, of1).trim()
        String value1 = s.substring(of1 + 1).trim()
        if (value1.startsWith('"')) {
            assert value1.endsWith('"')
            value1 = value1.substring(1, value1.length() - 1)
        }
        propsNameOrder.add(name1)
        props1.put(name1, value1)
    }

    void selfCheck() {
        HashSet<String> namesThis = new HashSet<>(propsNameOrder)
        if (namesThis.size() != propsNameOrder.size()) {
            HashSet<String> badProps = new HashSet<>()
            namesThis.each {
                int count1 = getPropsCount(it, propsNameOrder)
                if (count1 != 1) {
                    badProps.add(it)
                }
            }
            throw new Exception("found dup props ${badProps.size()} : ${badProps}")
        }
    }

    int getPropsCount(String prop, Collection<String> v) {
        return v.findAll { it == prop }.size()
    }

    void compareProps(JrRunnerChecker other) {
        this.selfCheck()
        other.selfCheck()

        HashSet<String> namesThis = new HashSet<>(propsNameOrder)
        HashSet<String> namesOthers = new HashSet<>(other.propsNameOrder)
        namesThis.removeAll(ignoreProps)
        namesOthers.removeAll(ignoreProps)
        if (namesThis.size() != namesOthers.size()) {
            HashSet<String> namesOthers2 = new HashSet<>(other.propsNameOrder)
            HashSet<String> namesThis2 = new HashSet<>(propsNameOrder)
            namesThis2.removeAll(ignoreProps)
            namesOthers2.removeAll(ignoreProps)
            namesThis2.removeAll(namesOthers)
            namesOthers2.removeAll(namesThis)
            if (namesOthers2.size() != 0) {
                throw new Exception("${namesOthers2}")
            }
            if (namesThis2.size() != 0) {
                throw new Exception("${namesThis2}")
            }
            throw new Exception("failed")
        }
        HashSet<String> namesThis3 = new HashSet<>(propsNameOrder)
        namesThis3.removeAll(ignoreProps)
        List<String> namesDiff = []
        namesThis3.each {
            String valueThis = props1.get(it)
            String valueOthers = other.props1.get(it)
            if (valueThis != valueOthers) {
                namesDiff.add(it)
            }
        }
        namesDiff.removeAll(ignoreDiffPropsValue)
        if (namesDiff.size() != 0) {
            throw new Exception("found diff values ${namesDiff.join(', ')}")
        }


        ArrayList<String> namesThis4 = new ArrayList<>(propsNameOrder)
        ArrayList<String> namesOthers4 = new ArrayList<>(other.propsNameOrder)
        namesThis4.removeAll(ignoreProps)
        namesOthers4.removeAll(ignoreProps)
        String lineBefore
        for (i in 0..<namesThis4.size()) {
            String this1 = namesThis4[i]
            String other1 = namesOthers4[i]
            if (this1 != other1) {
                throw new Exception("Diff prop order at ${i} , line4f=${lineBefore} : ${this1} vs ${other1}")
            }
            lineBefore=this1
        }
    }

    public static List<String> ignoreDiffPropsValue = [
            'JRR_HOSTNAME', 'JRR_CLASSPATH', 'JRR_ORIGINAL_ARGS',
            'JRR_SEPARATOR_CLASSPATH', 'JRR_ALWAYS_OPTS', 'JRR_CURRENT_DIR',
    ]

    public static List<String> ignoreProps = [
            'JRR_JOINT_NEXT_ARG',
            'JRR_CONSTANT_jrrrunnernofork', 'JRR_CONSTANT_jrrrunnerfork',
            'JRR_CURRENT_PROCESS_PID', 'JRR_UNAME_OUT', 'JRR_FORK_CMD', 'JRR_FORKED_PROCESS_PID', 'JRR_FORK_DISOWN',
    ]


    String normalizeLine(String line) {
        line = line.trim()
        if (line.isEmpty()) {
            return null
        }
        String lowerCase = line.toLowerCase()
        if (lowerCase.startsWith('#')) {
            return null
        }

        if (lowerCase.startsWith('rem')) {
            return null
        }
        if (lowerCase.startsWith('set ')) {
            line = line.substring(4)
        }
        return line.trim()
    }


    static void check1() {
        JrRunnerChecker checkerSh = new JrRunnerChecker()
        JrRunnerChecker checkerBat = new JrRunnerChecker()
        checkerSh.loadProps(JrrStarterOsSpecificFilesFirstDownload.gr_dot_sh.resolveToFile())
        checkerBat.loadProps(JrrStarterOsSpecificFilesFirstDownload.gr_dot_bat.resolveToFile())
        checkerSh.compareProps(checkerBat)
    }

}
