package com.carestream.demo.weakreference;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
public class MemoryUsageDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
	MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
	System.out.println(usage.getUsed());
    }

}
