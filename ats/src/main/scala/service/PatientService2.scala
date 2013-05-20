package service

object PatientService2 extends CSDMService {
  
  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/csdm/PatientService.csdm/PatientServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://service.remote.csdm.carestream.com\"", action, req).getOrElse("")
  }

   
  def listObjects(patientInternalID: String, insType: String = "all")(implicit fw: java.io.FileWriter) = {
    val filter = """
      <ser:listObjects>
         <!--Optional:-->
         <ser:filter>
        <![CDATA[<?xml version="1.0"?>
        <patientListObjectsRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://www.carestreamhealth.com/CSI/CSDM/1/Schema">
          <patientInternalId>%s</patientInternalId>
          <type>%s</type>
          <current>true</current>
        </patientListObjectsRequest>
        ]]>
	</ser:filter>
      </ser:listObjects>
      """ format (patientInternalID, insType)
    apply("listObjects", filter)
  }
 
}