package service

import scala.xml.XML
import conf.Conf

object AcquisitionService extends CSDMService {

  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/AcquisitionService.AcquisitionServiceHttpSoap12Endpoint/", "xmlns:acq=\"http://acquisition.services.LocalWS.pas.carestreamhealth.com\"", action, req).getOrElse("")
  }

  def acquire(acqInfo: String)(implicit fw: java.io.FileWriter) = {
    var acquisitionResult = ""
    synchronized {
      val acqRes = startAcquisition(acqInfo)
      val acqResXml = XML.loadString(acqRes)
      val acquisitionSessionID = acqResXml \\ ("session") \ ("parameter") \ ("@value") text

      java.lang.Thread.sleep(Conf.acqWait)
      //until get acquisition result

      var tyrAcqTime = 30
      do {
        Thread.sleep(2000)
        acquisitionResult = AcquisitionService.getAcquisitionResult(acquisitionSessionID)
        tyrAcqTime -= 1
        println("tyrAcqTime: " + tyrAcqTime)
        if (acquisitionResult.contains("not found") || acquisitionResult.contains("unknown command") || acquisitionResult.contains("acquisition failed")) {
          tyrAcqTime = 0
        }
      } while (!acquisitionResult.contains("code=\"0\"") && tyrAcqTime > 0)
    }
    acquisitionResult
  }

  def setAsynAcqPatientInfo(asyncAcqPatientInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
       <acq:setAsynAcqPatientInfo>
         <!--Optional:-->
         <acq:asyncAcqPatientInfo>
          <![CDATA[
                %s
          ]]>
         </acq:asyncAcqPatientInfo>
      </acq:setAsynAcqPatientInfo>
      """ format (asyncAcqPatientInfo)
    apply("setAsynAcqPatientInfo", input)
  }

  private def startAcquisition(acqInfo: String)(implicit fw: java.io.FileWriter) = {
    val req = """
         <acq:startAcquisition>
         <!--Optional:-->
	         <acq:acqInfo>
			<![CDATA[
			 %s
			]]>
	      </acq:acqInfo>
	      </acq:startAcquisition>
	      """ format (acqInfo)
    synchronized {
      apply("startAcquisition", req)
    }
  }

  private def getAcquisitionResult(acquisitionSessionID: String)(implicit fw: java.io.FileWriter) = {
    val req = """
         <acq:getAcquisitionResult>
         <!--Optional:-->
	         <acq:acquisitionSessionID>
			 %s
	      </acq:acquisitionSessionID>
	      </acq:getAcquisitionResult>
	      """ format (acquisitionSessionID)
    apply("getAcquisitionResult", req)
  }

  def queryDevices(sensorTypes: Seq[String])(implicit fw: java.io.FileWriter): String = {

    val sensorTypeList = sensorTypes.map("""<parameter key="type" value="%s" />""".format(_)).mkString("\n")
    val input = s"""
      <trophy type="request" version="1.0">
          <query_devices>
            <!-- sensorType vaue range[PANO,CEPH,CR,IO,3D,VL,SC]-->
            $sensorTypeList
          </query_devices>
        </trophy>
      """
    queryDevices(input)
  }

  def queryDevices(sensorTypeList: String)(implicit fw: java.io.FileWriter): String = {
    val input = s"""
      <acq:queryDevices><!--Optional:-->
         <acq:sensorTypeList>
            <![CDATA[
            $sensorTypeList
            ]]>
    </acq:sensorTypeList>
      </acq:queryDevices>
      """
    apply("queryDevices", input)
  }

  def queryLines(deviceIds: Seq[String])(implicit fw: java.io.FileWriter): String = {

    val deviceIdList = deviceIds.map("""<parameter key="id" value="%s" />""".format(_)).mkString("\n")
    val input = s"""
        <trophy type="request" version="1.0">
          <query_lines>
            $deviceIdList
           </query_lines>
        </trophy>
      """
    queryLines(input)
  }

  def queryLines(deviceIdList: String)(implicit fw: java.io.FileWriter): String = {
    val input = s"""
      <acq:queryLines><!--Optional:-->
         <acq:deviceIdList>
        <![CDATA[
            $deviceIdList
        ]]> 
    </acq:deviceIdList>
      </acq:queryLines>
      """
    apply("queryLines", input)
  }

}