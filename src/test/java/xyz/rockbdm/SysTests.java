package xyz.rockbdm;

import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.management.OperatingSystemMXBean;

public class SysTests {

    @Test
    public void doSomething() {
        while (true) {
            ThreadLocalRandom a = RandomUtil.getRandom();
            System.out.println(a);
        }
    }

    @Test
    public void cpuLoad() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double a = operatingSystemMXBean.getSystemCpuLoad();
        System.out.println(a * 100);
    }
}
