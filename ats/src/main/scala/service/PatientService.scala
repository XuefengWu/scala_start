package service

object PatientService extends CSDMService {
  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/PatientService.PatientServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", action, req).getOrElse("")
  }

  def findAllPatient()(implicit fw: java.io.FileWriter) = {
    val filter = """
         <trophy type="request" version="1.0">
    		  <filter>   
    		  </filter>
         </trophy>
      """
    findPatient(filter)
  }

  def findPatient(filter: String)(implicit fw: java.io.FileWriter) = {
    val input = """
            <ser:findPatient> 
         <ser:filter>
    	<![CDATA[
         %s
       ]]>
	 </ser:filter>
      </ser:findPatient>
      """ format (filter)
    apply("findPatient", input)
  }

  def queryPatients(req: String)(implicit fw: java.io.FileWriter) = {
    val filter = "<ser:queryPatients>\n<ser:filter>\n<![CDATA[\n%s\n]]>\n</ser:filter>\n</ser:queryPatients>" format (req)
    apply("queryPatients", filter)
  }

    def setPatient(req: String,patientUid:String)(implicit fw: java.io.FileWriter) = {
    val patientInfo = """<ser:setPatient>
	    <ser:patientInfo><![CDATA[ %s ]]></ser:patientInfo>
	    <ser:patientInternalID>%s</ser:patientInternalID>
      </ser:setPatient>""" format (req,patientUid)
    apply("setPatient", patientInfo)
  }

  def createPatient(req: String)(implicit fw: java.io.FileWriter) = {
    val patientInfo = "<ser:createPatient>\n<ser:patientInfo><![CDATA[\n%s\n]]></ser:patientInfo>\n</ser:createPatient>" format (req)
    apply("createPatient", patientInfo)
  }

  def deletePatient(patientInternalID: String)(implicit fw: java.io.FileWriter) = {
    val patientInfo = """
      <ser:deletePatient>
         <ser:patientInternalID>%s</ser:patientInternalID>
      </ser:deletePatient>
      """ format (patientInternalID)
    apply("deletePatient", patientInfo)
  }

  def listObjects(patientInternalID: String, insType: String = "all")(implicit fw: java.io.FileWriter) = {
    val filter = """
      <ser:listObjects>
         <!--Optional:-->
         <ser:filter>
		<![CDATA[
		 <trophy type="request" version="1.0">
		     <filter>
		         <parameter key="patient_internal_id" value="%s"/>
		         <parameter key="type" value="%s"/>
		     </filter>
		 </trophy>
		]]>
		</ser:filter>
		      </ser:listObjects>
      """ format (patientInternalID, insType)
    apply("listObjects", filter)
  }

  def getPatient(patientInternalID: String)(implicit fw: java.io.FileWriter) = {
    val input = """
            <ser:getPatient>
         <!--Optional:-->
         <ser:patientInternalID>%s</ser:patientInternalID>
      </ser:getPatient>
      """ format (patientInternalID)
    apply("getPatient", input)
  }

  def execFindPatient(sessionUid: String, count: Int)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:execFindPatient>
         <!--Optional:-->
         <ser:count>%d</ser:count>
         <!--Optional:-->
         <ser:sessionUid>%s</ser:sessionUid>
      </ser:execFindPatient>
      """ format (count, sessionUid)
    apply("execFindPatient", input)
  }

  def initFindPatient(filter: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:initFindPatient>
         <!--Optional:-->
         <ser:filter>
		<![CDATA[
	         %s
	       ]]>
	  	</ser:filter>
      </ser:initFindPatient>
      """ format (filter)
    apply("initFindPatient", input)
  }
}