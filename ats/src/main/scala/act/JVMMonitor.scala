package act

import javax.management.remote.JMXServiceURL
import javax.management.remote.JMXConnectorFactory
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ClassLoadingMXBean
import java.lang.management.MemoryPoolMXBean
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import java.util.Date

object JVMMonitor extends App {

  val url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:3333/jmxrmi")

  val jmxConnector = JMXConnectorFactory.connect(url)

  val serverConn = jmxConnector.getMBeanServerConnection()

  val memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
    ManagementFactory.MEMORY_MXBEAN_NAME, classOf[MemoryMXBean])

  val memoryPoolMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
    ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=Perm Gen", classOf[MemoryPoolMXBean])

  val classLoadingMXBean = ManagementFactory.newPlatformMXBeanProxy(serverConn,
    ManagementFactory.CLASS_LOADING_MXBEAN_NAME, classOf[ClassLoadingMXBean])

  val file = new java.io.File("d:/jvm.log")
  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  while (true) {
    val sb = new StringBuffer()
    sb.append(sdf.format(new Date()))
    sb.append("\t")
    sb.append(memoryMXBean.getHeapMemoryUsage().getUsed())
    sb.append("\t")
    sb.append(memoryPoolMXBean.getUsage().getUsed())
    sb.append("\t")
    sb.append(classLoadingMXBean.getLoadedClassCount())
    sb.append("\t")
    sb.append(classLoadingMXBean.getUnloadedClassCount())
    sb.append("\t")
    sb.append("\n")
    FileUtils.writeStringToFile(file,sb.toString(),true)
    Thread.sleep(500);
  }

}