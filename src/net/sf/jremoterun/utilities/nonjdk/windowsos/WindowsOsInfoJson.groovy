package net.sf.jremoterun.utilities.nonjdk.windowsos

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaObjectPropsDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectProps2Dumper
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.ComputerSystem
import oshi.hardware.HardwareAbstractionLayer
import oshi.hardware.NetworkIF
import oshi.hardware.PhysicalMemory
import oshi.hardware.UsbDevice;

import java.util.logging.Logger;

@CompileStatic
class WindowsOsInfoJson {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SystemInfo si = new SystemInfo();
    public String lineSep = '\n'
    public long[] oldTicks = new long[oshi.hardware.CentralProcessor.TickType.values().length];
    public ObjectDumperProvider javaObjectPropsDumper = new ObjectDumperProvider( new JavaObjectPropsDumper([new FieldRef(new ClRef(oshi.hardware.common.AbstractNetworkIF),'networkInterface')]));

    public static List ignoreFieldNames2 = [
            'serialNumber',
            'hardwareUUID',
            'graphicsCards',
            'uuid',
            ]

    public static List<String> ignoreFieldNames = [        'available',
                                                   'networkIFs',
                                                   'readBytes',
                                                   'reads',
                                                   'timeStamp',
                                                   'transferTime',
                                                   'writeBytes',
                                                   'writes',
                                                   'currentQueueLength',
                                                   'displays',
                                                   'virtualMemory',
                                                   'contextSwitches',
                                                   'interrupts',
                                                   'processorCpuLoadTicks',
                                                   'systemCpuLoadTicks',
                                                   'soundCards',
                                                   'currentFreq',

    ]


    JavaObjectProps2Dumper createDumper(){
        JavaObjectProps2Dumper javaObjectProps2DumperD = new JavaObjectProps2Dumper()
        javaObjectProps2DumperD.ignoreFields.addAll(ignoreFieldNames)
        return javaObjectProps2DumperD;
    }

    public String getHw() {
        ObjectDumperProvider javaObjectPropsDumper2 = new ObjectDumperProvider( createDumper());
        List r = []
        HardwareAbstractionLayer hardware = si.getHardware();
        r.add(hardware)
        return JsonOutput.prettyPrint(JsonOutput.toJson(javaObjectPropsDumper2.dumpObject(r)));
    }



    public double cpuData(CentralProcessor proc) {
        double d = proc.getSystemCpuLoadBetweenTicks(oldTicks);
        oldTicks = proc.getSystemCpuLoadTicks();
        return d;
    }


}
