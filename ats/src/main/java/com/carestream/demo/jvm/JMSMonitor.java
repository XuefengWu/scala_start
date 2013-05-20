package com.carestream.demo.jvm;


import java.io.File;
import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.io.FileUtils;

/**
 * -Dcom.sun.management.jmxremote.port=3333 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=10.112.37.92 
 * 
 * @author 19002850
 *
 */
public class JMSMonitor {

    private static final File file = new java.io.File("d:/jvm.log");
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        // connect to a remote VM using JMX RMI
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:3333/jmxrmi");

        JMXConnector jmxConnector = JMXConnectorFactory.connect(url);

        MBeanServerConnection serverConn = jmxConnector.getMBeanServerConnection();

        /*
        ObjectName objName = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);

        
        // Get standard attribute "VmVendor"
        String vendor = (String)serverConn.getAttribute(objName, "VmVendor");
        echo(vendor);

        ThreadMXBean threadBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
                ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
        echo(threadBean.getThreadCount());
        */
        
        MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
                ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        //echo(memoryMXBean.getHeapMemoryUsage().getMax());
        
        //echo(memoryMXBean.getHeapMemoryUsage().getCommitted());
        
        MemoryPoolMXBean memoryPoolMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
                ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE+",name=Perm Gen", MemoryPoolMXBean.class);
        //echo(memoryPoolMXBean.getUsage().getInit());
        //echo(memoryPoolMXBean.getUsage().getMax());
        //echo(memoryPoolMXBean.getUsage().getCommitted());
        
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
                ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);
        
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FileUtils.writeStringToFile(file,"");
        while(true){
            StringBuffer sb = new StringBuffer();
            sb.append(sdf.format(new Date()));
            sb.append("\t");
            sb.append(memoryMXBean.getHeapMemoryUsage().getUsed());
            sb.append("\t");
            sb.append(memoryPoolMXBean.getUsage().getUsed());
            sb.append("\t");
            sb.append(classLoadingMXBean.getLoadedClassCount());
            sb.append("\t");
            sb.append(classLoadingMXBean.getUnloadedClassCount());
            sb.append("\t");
            sb.append("\n");
            echo(sb);
            Thread.sleep(3000);
        }
    }

    private static void echo(Object msg) throws IOException {
        System.out.println(msg);
        FileUtils.writeStringToFile(file,msg.toString(),true);
    }

}
