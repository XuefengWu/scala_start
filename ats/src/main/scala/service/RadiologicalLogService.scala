package service

object RadiologicalLogService extends CSDMService {
  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/RadiologicalLogService.RadiologicalLogServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", action, req).getOrElse("")
  }

  def createRadioLogEntry(radio_log: String)(implicit fw: java.io.FileWriter) = {
    val req = """
      <ser:createRadioLogEntry>
         <!--Optional:-->
         <ser:radioLogEntryInfoXml>
            <![CDATA[
                %s
            ]]>
            </ser:radioLogEntryInfoXml>
      </ser:createRadioLogEntry>
      """ format (radio_log)
    apply("createRadioLogEntry", req)
  }

  def exportRadioLog(filter: String)(implicit fw: java.io.FileWriter) = {
    val req = """
      <ser:exportRadioLog>
         <!--Optional:-->
         <ser:filter>
            <![CDATA[
                %s
            ]]>
            </ser:filter>
      </ser:exportRadioLog>
      """ format (filter)
    apply("exportRadioLog", req)
  }

  def initFindRadioLogForPatient(pid: String)(implicit fw: java.io.FileWriter) = {
    val req = s"""
         <trophy type="request" version="1.0">
           <filter>
               <parameter key="patient_internal_id" value="$pid" />
           </filter>
         </trophy> 
      """
    initFindRadioLog(req)
  }

  def initFindRadioLog(filter: String)(implicit fw: java.io.FileWriter) = {
    val req = s"""
      <ser:initFindRadioLog>
         <!--Optional:-->
         <ser:filter>
        <![CDATA[
         $filter
        ]]>
        </ser:filter>
      </ser:initFindRadioLog>
      """
    apply("initFindRadioLog", req)
  }

  def execFindRadioLog(sessionId: String)(implicit fw: java.io.FileWriter) = {
    val req = s"""
      <ser:execFindRadioLog>
         <!--Optional:-->
         <ser:count>50</ser:count>
         <!--Optional:-->
         <ser:sessionId>$sessionId</ser:sessionId>
      </ser:execFindRadioLog>
      """
    apply("execFindRadioLog", req)
  }

}